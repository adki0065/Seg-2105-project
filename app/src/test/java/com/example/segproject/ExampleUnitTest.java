
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

    /*Deliverable 4 Unit Test Cases*/

    @Test
    public void ratingBarNotEmpty()throws Exception {
        assertNotNull(R.id.rating_bar);
    }

    @Test
    public void timeNotEmpty()throws Exception {
        assertNotNull(R.id.time_picker);
    }

    @Test
    public void dateNotEmpty()throws Exception {
        assertNotNull(R.id.date_picker);
    }

    @Test
    public void canReview()throws Exception {
        assertNotNull(R.id.edit_clinic_button);
    }

    @Test
    public void canViewReviews()throws Exception {
        assertNotNull(R.id.clinic_reviews_button);
    }

    @Test
    public void canBookAppointment()throws Exception {
        assertNotNull(R.id.delete_clinic_button);
    }

    @Test
    public void searchWaitTimeNotEmpty()throws Exception {
        assertNotNull(R.id.clinic_wait_time);
    }

    @Test
    public void searchAddressNotEmpty()throws Exception {
        assertNotNull(R.id.clinic_address);
    }

    @Test
    public void searchPhoneNumberNotEmpty()throws Exception {
        assertNotNull(R.id.clinic_phone);
    }

    @Test
    public void searchClinicNameNotEmpty()throws Exception {
        assertNotNull(R.id.clinic_title);
    }
}