package pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.domain;

import jakarta.persistence.*;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.dto.AssessmentDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;



import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.*;


@Entity
@Table(name = "assessment")
public class Assessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String review;
    private LocalDateTime reviewDate;

    @ManyToOne
    private Institution institution;

    @ManyToOne
    private Volunteer volunteer;


    public Assessment() {
    }

    public Assessment(Institution institution, Volunteer volunteer, AssessmentDto assessmentDto) {
        setInstitution(institution);
        setVolunteer(volunteer);
        setReview(assessmentDto.getReview());
        setReviewDate(DateHandler.toLocalDateTime(assessmentDto.getReviewDate()));
        addAssessmentToInstitution();
        verifyInvariants();

    }

    public void update(AssessmentDto assessmentDto) {
        setReview(assessmentDto.getReview());
        setReviewDate(DateHandler.toLocalDateTime(assessmentDto.getReviewDate()));

        verifyInvariants();
    }

    public Integer getId() {
        return id;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public LocalDateTime getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(LocalDateTime reviewDate) {
        this.reviewDate = reviewDate;
    }

    public Institution getInstitution() {
        return institution;
    }

    public void setInstitution(Institution institution) {
        this.institution = institution;
    }

    public Volunteer getVolunteer() {
        return volunteer;
    }

    public void setVolunteer(Volunteer volunteer) {
        this.volunteer = volunteer;
    }

    public void addAssessmentToInstitution() {
        this.institution.addAssessment(this);
    }

    private void verifyInvariants() {
        reviewLength();
        institutionAlreadyEvaluated();
        canBeEvaluated();
    }

    private void reviewLength() {
        if (this.review == null || this.review.length() < 10) {
            throw new HEException(ASSESSMENT_INVALID_REVIEW_LENGTH);
        }
    }

    private void institutionAlreadyEvaluated() {
        if (this.institution.checkAssessmentsWithVolunteer(this.volunteer)){
            throw new HEException(ASSESSMENT_INSTITUTION_ALREADY_EVALUATED);
        }
    }

    private void canBeEvaluated(){
        if (!this.institution.checkForCompletedActivity()){
            throw new HEException(ASSESSMENT_NO_COMPLETED_ACTIVITIES);
        }
    }









}