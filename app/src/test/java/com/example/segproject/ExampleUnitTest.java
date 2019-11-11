package com.example.segproject;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void nameNotEmpty() throws Exception{
        assertNotNull(R.id.NameField);
    }

    @Test
    public void passwordNotEmpty() throws Exception {
        assertNotNull(R.id.PasswordField);
    }

    @Test
    public void userAdminCheck() throws Exception {
        assertEquals("admin",R.id.UsernameField);
    }

    @Test
    public void passwordAdminCheck() throws Exception {
        assertEquals("5T5ptQ",R.id.PasswordField);
    }

    @Test
    public void roleEmployeeCheck() throws Exception {
        assertEquals("Employee",R.id.toggleButton);
    }

    @Test
    public void rolePatientCheck() throws Exception {
        assertEquals("Patient",R.id.toggleButton);
    }
}