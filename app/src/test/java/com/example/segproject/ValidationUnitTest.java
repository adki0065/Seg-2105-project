package com.example.segproject;

import org.junit.Test;

import static org.junit.Assert.*;


public class ValidationUnitTest {
    @Test
    public void NameValid() throws Exception {
        assertNotNull("Name exists", Util.ValidateName(""));
        assertNull("Name is valid", Util.ValidatePassword("Robert"));
    }

    @Test
    public void PasswordExists() throws Exception {
        assertNotNull("Password exists", Util.ValidatePassword(""));
        assertNotNull("Password greater than 4", Util.ValidatePassword("123"));
        assertNotNull("Password doesn't contain spaces", Util.ValidatePassword("12 345"));
        assertNull("Password is valid", Util.ValidatePassword("12345"));
    }

    @Test
    public void validateUsername() throws Exception {
        assertNotNull("Username exists", Util.ValidateUsername(""));
        assertNotNull("Username greater than 4", Util.ValidateUsername("123"));
        assertNotNull("Username doesn't contain spaces", Util.ValidateUsername("12 345"));
        assertNull("Username is valid", Util.ValidatePassword("robert"));
    }

    //new for deliverable 3
    @Test
    public void validatePhone() throws Exception{
        assertNotNull("Number is too short", Util.ValidatePhone("123"));
        assertNotNull("Number is too long", Util.ValidatePhone("1234567891011"));
        assertNull("Number is acceptable", Util.ValidatePhone("6138675309"));
        assertNotNull("Number contains whitespace", Util.ValidatePhone("613867 5309"));
    }

    @Test
    public void validateAddress() throws Exception{

        assertNull("Working address", Util.ValidateAddress("123 main street"));
        assertNotNull("Address too short", Util.ValidateAddress("1 b"));

    }
}

