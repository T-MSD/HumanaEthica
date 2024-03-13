package pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain;

import jakarta.persistence.*;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.domain.Participation;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.domain.Assessment;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.domain.Enrollment;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution;


import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue(User.UserTypes.VOLUNTEER)
public class Volunteer extends User {


    @OneToMany(mappedBy = "volunteer")
    private List<Participation> participationList = new ArrayList<>();

    @ManyToOne
    private Institution institution;

    @OneToOne
    private Activity activity;

    @OneToMany(mappedBy = "volunteer")
    private List<Enrollment> enrollments = new ArrayList<>();

    @OneToMany(mappedBy = "volunteer")
    private List<Assessment> assessments = new ArrayList<>();



    @ManyToOne
    private Institution institution;

    @OneToOne
    private Activity activity;

    @OneToMany(mappedBy = "volunteer")
    private List<Enrollment> enrollments = new ArrayList<>();

    public List<Enrollment> getEnrollments() {
        return enrollments;
    }

    public Volunteer() {
    }

    public Volunteer(String name, String username, String email, AuthUser.Type type, State state) {
        super(name, username, email, Role.VOLUNTEER, type, state);
    }

    public Volunteer(String name, State state) {
        super(name, Role.VOLUNTEER, state);
    }

    public List<Participation> getParticipationList() {
        return participationList;
    }

    public void addParticipation(Participation participation){
        this.participationList.add(participation);
    }

    public void removeParticipation(Participation participation){
        this.participationList.remove(participation);
    }

    public List<Enrollment> getEnrollments() {
        return enrollments;
    }

    public void addEnrollment(Enrollment enrollment) {
        enrollments.add(enrollment);
    }

}

