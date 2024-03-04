package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.domain;

import jakarta.persistence.*;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;

import java.time.LocalDateTime;

@Entity
@Table(name = "enrollment")
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String motivation;
    private LocalDateTime enrollmentDateTime;

    @ManyToOne
    private Activity activity;

    @ManyToOne
    private Volunteer volunteer;

    public Enrollment() {
    }

    public boolean checkMotivation(String motivation) {
        return motivation.length() >= 10;
    }
}
