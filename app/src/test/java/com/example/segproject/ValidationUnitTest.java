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
}
