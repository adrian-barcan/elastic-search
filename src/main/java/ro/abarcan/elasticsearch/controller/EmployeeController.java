package ro.abarcan.elasticsearch.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ro.abarcan.elasticsearch.domain.Employee;
import ro.abarcan.elasticsearch.service.ElasticSearchService;
import ro.abarcan.elasticsearch.service.EmployeeRepositoryService;

import java.io.IOException;

@RestController
public class EmployeeController {

    private final EmployeeRepositoryService employeeRepositoryService;
    private final ElasticSearchService elasticSearchService;

    public EmployeeController(EmployeeRepositoryService employeeRepositoryService, ElasticSearchService elasticSearchService) {
        this.employeeRepositoryService = employeeRepositoryService;
        this.elasticSearchService = elasticSearchService;
    }

    @PostMapping("/v1/employee")
    public void addEmployee(@RequestBody final Employee employee) throws IOException {
        elasticSearchService.addEmployee(employee);
    }

    @GetMapping("/v1/employee/{id}")
    public Employee getEmployee(@PathVariable final String id) {
        return employeeRepositoryService.getEmployee(id);
    }


    @GetMapping("/v1/employees")
    public Page<Employee> getAllEmployees(Pageable pageable) {
        return employeeRepositoryService.getAllEmployees(pageable);
    }

    @DeleteMapping("/v1/employee/{id}")
    public void deleteEmployee(@PathVariable final String id) {
        employeeRepositoryService.deleteEmployee(id);
    }

    @GetMapping("/v1/employees/count")
    public Long countEmployees() {
        return employeeRepositoryService.countEmployees();
    }

    @GetMapping("/v1/employees/search")
    public Page<Employee> getEmployeesByFirstName(@RequestParam(required = false) final String query, Pageable pageable) throws IOException {
        return elasticSearchService.search(query, pageable);
    }

    @PostMapping("/v1/employees/init")
    public void initEmployees() {
        elasticSearchService.generateEmployees();
    }
}
