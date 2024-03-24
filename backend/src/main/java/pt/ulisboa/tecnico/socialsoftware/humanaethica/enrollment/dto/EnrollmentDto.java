package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.dto;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.domain.Enrollment;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler;

public class EnrollmentDto {
    private Integer id;
    private String motivation;

    private String enrollmentDateTime;

    private Integer activityId;
    private Boolean participating;
    private String volunteerName;

    public EnrollmentDto() {}

    public EnrollmentDto(Enrollment enrollment) {
        this.id = enrollment.getId();
        this.motivation = enrollment.getMotivation();
        this.enrollmentDateTime = DateHandler.toISOString(enrollment.getEnrollmentDateTime());
        this.activityId = enrollment.getActivity().getId();
        this.volunteerName = enrollment.getVolunteer().getName();
        this.participating = enrollment.getVolunteer().getParticipations().stream().anyMatch(p -> p.getActivity().getId() == enrollment.getActivity().getId());
    }

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

    public void setEnrollmentDateTime(String enrollmentDateTime) {
        this.enrollmentDateTime = enrollmentDateTime;
    }


    public Integer getActivityId() {
        return activityId;
    }

    public void setActivityId() {
        this.activityId = activityId;
    }

    public Boolean getParticipating() {
        return this.participating;
    }

    public void setParticipating(final Boolean participating) {
        this.participating = participating;
    }

    public String getVolunteerName() {
        return this.volunteerName;
    }

    public void setVolunteerName(final String volunteerName) {
        this.volunteerName = volunteerName;
    }
}

