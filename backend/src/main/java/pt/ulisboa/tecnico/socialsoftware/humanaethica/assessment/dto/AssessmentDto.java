package pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.dto;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.domain.Assessment;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.dto.InstitutionDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler;


public class AssessmentDto {

    private Integer id;
    private String review;
    private String reviewDate;

    private InstitutionDto institution;

    public AssessmentDto() {
    }



    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(String reviewDate) {
        this.reviewDate = reviewDate;
    }

    public InstitutionDto getInstitution() {
        return institution;
    }

    public void setInstitution(InstitutionDto institution) {
        this.institution = institution;
    }


    @Override
    public String toString() {
        return "AssessmentDto{" +
                "id=" + id +
                ", review='" + review + '\'' +
                ", reviewDate=" + reviewDate +
                ", institution=" + institution +
                '}';
    }

}