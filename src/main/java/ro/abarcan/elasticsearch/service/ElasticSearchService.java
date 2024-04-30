package ro.abarcan.elasticsearch.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.server.ResponseStatusException;
import ro.abarcan.elasticsearch.domain.Employee;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.IntStream;

@Slf4j
@Service
public class ElasticSearchService {

    private static final String EMPLOYEE_INDEX = "employee";
    private final ElasticsearchClient elasticsearchClient;

    public ElasticSearchService(ElasticsearchClient elasticsearchClient) {
        this.elasticsearchClient = elasticsearchClient;
    }

    public void addEmployee(Employee employee) throws IOException {
        IndexResponse response = elasticsearchClient.index(s -> s
                .index(EMPLOYEE_INDEX)
                .id(employee.getId())
                .document(employee));
        log.info("Indexed with version {}", response.version());
    }

    public Page<Employee> search(String query, Pageable pageable) throws IOException {
        final var response = elasticsearchClient.search(s -> buildSearchRequest(s, query, pageable), Employee.class);
        return buildPage(response, pageable);
    }

    private SearchRequest.Builder buildSearchRequest(SearchRequest.Builder searchRequest,
                                                     String query,
                                                     Pageable pageable) {
        searchRequest.index(EMPLOYEE_INDEX)
                .from((int) pageable.getOffset())
                .size(pageable.getPageSize());

        if (!ObjectUtils.isEmpty(query)) {
            String finalQuery = query.toLowerCase();
            searchRequest.query(q -> q.bool(b -> b
                    .should(sh -> sh.wildcard(w -> w.field("firstName").value("*" + finalQuery + "*")))
                    .should(sh -> sh.wildcard(w -> w.field("lastName").value("*" + finalQuery + "*")))
                    .should(sh -> sh.wildcard(w -> w.field("position").value("*" + finalQuery + "*")))
                    .should(sh -> sh.wildcard(w -> w.field("city").value("*" + finalQuery + "*")))));
        }
        return searchRequest;
    }

    private Page<Employee> buildPage(SearchResponse<Employee> response, Pageable pageable) {
        List<Employee> employeeList = response.hits().hits().stream()
                .filter(hit -> hit.source() != null)
                .map(hit -> {
                    Employee employee = hit.source();
                    log.info("Found employee {}, score {}", employee.getFirstName(), hit.score());
                    return employee;
                })
                .toList();

        long total = response.hits().total() != null ? response.hits().total().value() : 0;
        return new PageImpl<>(employeeList, pageable, total);
    }

    public void generateEmployees() {
        final var faker = new Faker();
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
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to add employee", e);
            }
        });
    }
}
