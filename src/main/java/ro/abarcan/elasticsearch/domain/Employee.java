package ro.abarcan.elasticsearch.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.math.BigDecimal;

@Data
@Document(indexName = "employee")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Employee {

    @Id
    @Field(name = "id")
    private String id;

    @Field(name = "firstName", type = FieldType.Text, normalizer = "lowercase")
    private String firstName;

    @Field(name = "lastName", type = FieldType.Text, normalizer = "lowercase")
    private String lastName;

    @Field(name = "age")
    private int age;

    @Field(name = "position", type = FieldType.Text, normalizer = "lowercase")
    private String position;

    @Field(name = "country", type = FieldType.Text, normalizer = "lowercase")
    private String country;

    @Field(name = "city", type = FieldType.Text, normalizer = "lowercase")
    private String city;

    @Field(name = "address", type = FieldType.Text, normalizer = "lowercase")
    private String address;

    @Field(name = "email", type = FieldType.Text, normalizer = "lowercase")
    private String email;

    @Field(name = "phone", type = FieldType.Text, normalizer = "lowercase")
    private String phone;

    @Field(name = "department", type = FieldType.Text, normalizer = "lowercase")
    private String department;

    @Field(name = "salary")
    private BigDecimal salary;
}
