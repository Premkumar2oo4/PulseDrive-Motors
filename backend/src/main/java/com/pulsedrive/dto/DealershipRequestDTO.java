package com.pulsedrive.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
public class DealershipRequestDTO {

    @NotBlank(message = "Dealership name is required")
private String name;

@NotBlank(message = "Address is required")
@Size(max = 1000, message = "Address cannot exceed 1000 characters")
private String address;

@NotBlank(message = "City is required")
private String city;

@NotBlank(message = "State is required")
private String state;

@Pattern(
        regexp = "^[0-9+\\- ]{7,20}$",
        message = "Invalid phone number"
)
private String phone;

@Email(message = "Invalid dealership email")
private String email;

private String openingHours;
private Double latitude;
private Double longitude;

    public DealershipRequestDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(String openingHours) {
        this.openingHours = openingHours;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}