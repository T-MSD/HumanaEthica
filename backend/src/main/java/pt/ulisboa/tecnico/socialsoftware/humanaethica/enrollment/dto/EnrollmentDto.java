package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.dto;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto.ActivityDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.domain.Enrollment;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.dto.UserDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler;

public class EnrollmentDto {
    private Integer id;
    private String motivation;
    private String enrollmentDateTime;
    private ActivityDto activity;
    private UserDto volunteer;

    // Constructors, getters, and setters...

    public EnrollmentDto() {
    }

    public EnrollmentDto(Enrollment Enrollment) {
        setId(Enrollment.getId());
        setMotivation(Enrollment.getMotivation()); ////check motivation in enrollment class
        setEnrollmentDateTime(DateHandler.toISOString(Enrollment.getEnrollmentDateTime()));
        setActivity(activity);
        setVolunteer(volunteer); //check if user is volunteer in enrollment class

        // Perform validation to ensure the user is a volunteer
        if (!volunteer.getRole().equals("VOLUNTEER")) {
            throw new IllegalArgumentException("User is not a volunteer");
        }
    }

    // Getters and setters...
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMotivation() {
        return motivation;
    }

    public void setMotivation(String motivation) {
        this.motivation = motivation;
    }

    public String getEnrollmentDateTime() {
        return enrollmentDateTime;
    }

    public void setEnrollmentDateTime(String string) {
        this.enrollmentDateTime = string;
    }

    public ActivityDto getActivity() {
        return activity;
    }

    public void setActivity(ActivityDto activity) {
        this.activity = activity;
    }

    public void setVolunteer(UserDto volunteer) {
        this.volunteer = volunteer;
    }
}
