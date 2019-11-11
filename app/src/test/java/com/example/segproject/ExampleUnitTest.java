package com.example.segproject;

import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import org.junit.Test;
import org.junit.runner.RunWith;

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
    public void usernameNotEmpty() throws Exception {
        assertNotNull(R.id.UsernameField);
    }

    @Test
    public void passwordNotEmpty() throws Exception {
        assertNotNull(R.id.PasswordField);
    }

    @Test
    public void loginButtonNotEmpty() throws Exception {
        assertNotNull(R.id.btnLogin);
    }

    @Test
    public void roleNotEmpty() throws Exception {
        assertNotNull(R.id.toggleButton);
    }
}