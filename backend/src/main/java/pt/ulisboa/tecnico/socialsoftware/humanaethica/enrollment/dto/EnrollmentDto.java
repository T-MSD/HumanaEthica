package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.dto;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto.ActivityDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.domain.Enrollment;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.dto.UserDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler;

public class EnrollmentDto {
    private Integer id;
    private String motivation;
    private String enrollmentDateTime;
    private ActivityDto activityDto;
    private UserDto volunteerDto;

    // Constructors, getters, and setters...

    public EnrollmentDto() {
    }

    public EnrollmentDto(Enrollment Enrollment) {
        setId(Enrollment.getId());
        setMotivation(Enrollment.getMotivation());
        setEnrollmentDateTime(DateHandler.toISOString(Enrollment.getEnrollmentDateTime()));
        setActivityDto(new ActivityDto(Enrollment.getActivity(), false));
        setVolunteerDto(new UserDto(Enrollment.getVolunteer()));
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

    public ActivityDto getActivityDto() {
        return activityDto;
    }

    public UserDto getVolunteerDto(){
        return volunteerDto;
    }
    

    public void setActivityDto(ActivityDto activity) {
        this.activityDto = activity;
    }

    public void setVolunteerDto(UserDto volunteer) {
        this.volunteerDto = volunteer;
    }
    
}
