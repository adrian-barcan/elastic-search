package ro.abarcan.elasticsearch.repository;


import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import ro.abarcan.elasticsearch.domain.Employee;

public interface EmployeeRepository extends ElasticsearchRepository<Employee, String> {
}
