package model;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table (name = "Users")
public class Users {

    private String firstName;
    private String lastName;
    private String contactNumber;

    @Id
    private String emailId;

    // Getters and Setters
}
