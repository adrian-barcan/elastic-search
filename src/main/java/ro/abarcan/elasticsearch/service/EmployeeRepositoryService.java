package ro.abarcan.elasticsearch.service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ro.abarcan.elasticsearch.domain.Employee;
import ro.abarcan.elasticsearch.repository.EmployeeRepository;

@Service
public class EmployeeRepositoryService {

    private final EmployeeRepository employeeRepository;

    public EmployeeRepositoryService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public Employee getEmployee(String id) {
        return employeeRepository.findById(id).orElse(null);
    }


    public Page<Employee> getAllEmployees(Pageable pageable) {
        return employeeRepository.findAll(pageable);
    }

    public void deleteEmployee(String id) {
        employeeRepository.deleteById(id);
    }

    public Long countEmployees() {
        return employeeRepository.count();
    }

}
