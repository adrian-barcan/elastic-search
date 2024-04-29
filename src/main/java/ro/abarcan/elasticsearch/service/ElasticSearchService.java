package ro.abarcan.elasticsearch.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.TotalHits;
import co.elastic.clients.elasticsearch.core.search.TotalHitsRelation;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ro.abarcan.elasticsearch.domain.Employee;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Slf4j
@Service
public class ElasticSearchService {

    private final ElasticsearchClient elasticsearchClient;

    public ElasticSearchService(ElasticsearchClient elasticsearchClient) {
        this.elasticsearchClient = elasticsearchClient;
    }

    public void addEmployee(Employee employee) throws IOException {
        IndexResponse response = elasticsearchClient.index(s -> s
                .index("employee")
                .id(employee.getId())
                .document(employee));
        log.info("Indexed with version " + response.version());
    }

    public Page<Employee> search(String query, Pageable pageable) throws IOException {
        if (query == null || query.isEmpty()) {
            SearchResponse<Employee> response = elasticsearchClient.search(s -> s
                            .index("employee")
                            .from((int) pageable.getOffset())
                            .size(pageable.getPageSize()),
                    Employee.class);
            Result result = getResult(response);
            return new PageImpl<>(result.employeeList(), pageable, result.total().value());
        } else {
            String finalQuery = query.toLowerCase();
            SearchResponse<Employee> response = elasticsearchClient.search(s -> s
                            .index("employee")
                            .from((int) pageable.getOffset())
                            .size(pageable.getPageSize())
                            .query(q -> q
                                    .bool(b -> b
                                            .should(sh -> sh
                                                    .wildcard(w -> w
                                                            .field("firstName")
                                                            .value("*" + finalQuery + "*")
                                                    ))
                                            .should(sh -> sh
                                                    .wildcard(w -> w
                                                            .field("lastName")
                                                            .value("*" + finalQuery + "*")
                                                    ))
                                            .should(sh -> sh
                                                    .wildcard(w -> w
                                                            .field("position")
                                                            .value("*" + finalQuery + "*")
                                                    ))
                                            .should(sh -> sh
                                                    .wildcard(w -> w
                                                            .field("city")
                                                            .value("*" + finalQuery + "*")
                                                    ))
                                    )),
                    Employee.class);

            Result result = getResult(response);
            return new PageImpl<>(result.employeeList(), pageable, result.total().value());
        }
    }

    private static Result getResult(SearchResponse<Employee> response) {
        TotalHits total = response.hits().total();
        boolean isExactResult = total.relation() == TotalHitsRelation.Eq;

        if (isExactResult) {
            log.info("There are {} results", total.value());
        } else {
            log.info("There are more than {} results", total.value());
        }

        List<Hit<Employee>> hits = response.hits().hits();

        List<Employee> employeeList = new ArrayList<>();
        for (Hit<Employee> hit : hits) {
            Employee employee = hit.source();
            log.info("Found product {}, score {}", employee.getFirstName(), hit.score());
            employeeList.add(employee);
        }
        return new Result(total, employeeList);
    }

    private record Result(TotalHits total, List<Employee> employeeList) {
    }

    public void generateEmployees() {
        Faker faker = new Faker();
        IntStream.range(0, 1000000).parallel().forEach(i -> {
            Employee employee = new Employee();
            employee.setId(String.valueOf(i));
            employee.setFirstName(faker.name().firstName());
            employee.setLastName(faker.name().lastName());
            employee.setAge(faker.number().numberBetween(20, 60));
            employee.setPosition(faker.job().position());
            employee.setCountry(faker.address().country());
            employee.setCity(faker.address().city());
            employee.setAddress(faker.address().streetAddress());
            employee.setEmail(faker.internet().emailAddress());
            employee.setPhone(faker.phoneNumber().phoneNumber());
            employee.setDepartment(faker.commerce().department());
            employee.setSalary(BigDecimal.valueOf(faker.number().randomDouble(2, 30000, 100000)));
            try {
                addEmployee(employee);
            } catch (IOException e) {
                log.warn("Failed to add employee", e);
                throw new RuntimeException(e);
            }
        });
    }
}
