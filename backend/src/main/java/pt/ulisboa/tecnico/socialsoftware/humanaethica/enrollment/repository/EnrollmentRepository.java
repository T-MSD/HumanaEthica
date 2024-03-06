package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.domain.Enrollment;

@Repository
@Transactional
public interface EnrollmentRepository extends JpaRepository<Enrollment, Integer> {


    @Query("SELECT e FROM Enrollment e WHERE e.activity.id = :activityId")
    List<Enrollment> getEnrollmentsByActivityId(Integer activityId);

    

}

