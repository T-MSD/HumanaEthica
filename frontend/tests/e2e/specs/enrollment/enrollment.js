describe('Enrollment', () => {
  
  
    it('as a member, verify activity table has 3 instances and first activity has 0 applications', () => {
      cy.deleteAllButArs();
      cy.createDemoEntitiesTest1();
      cy.demoMemberLogin();

      // go to show activities form
      cy.get('[data-cy="institution"]').click();
      cy.get('[data-cy="activities"]').click();
  
      // Assert there are 3 activities
      cy.get('[data-cy="memberActivitiesTable"] tbody tr')
      .should('have.length', 3) 

      // Assert the first activity has 0 applications
      cy.get('[data-cy="memberActivitiesTable"] tbody tr')
        .eq(0)
        .children()
        .eq(3).should('contain', '0');

      cy.logout();
    });
    
  
    it('as a volunteer, apply for the first activity', () => {
      cy.demoVolunteerLogin();
      const MOTIVATION = 'Demo-Motivation';

      // go to show activities form
      cy.get('[data-cy="volunteerActivities"]').click();
  
      // Apply for the first activity
      cy.get('[data-cy="volunteerActivitiesTable"] tbody tr')
        .eq(0)
        .find('[data-cy="newEnrollment"]')
        .click();

      // fill form
      cy.get('[data-cy="motivationInput"]').type(MOTIVATION);

      // save form
      cy.get('[data-cy="saveEnrollment"]').click()

      cy.logout();
    });
    
  
    it('as a member, verify the first activity has 1 application and check enrollments', () => {
      cy.demoMemberLogin();
      const MOTIVATION = 'Demo-Motivation';

      // go to show activities form
      cy.get('[data-cy="institution"]').click();
      cy.get('[data-cy="activities"]').click();

      // Assert the first activity has 1 application
      cy.get('[data-cy="memberActivitiesTable"] tbody tr')
      .eq(0).children().eq(3).should('contain', 1 );

      // Check enrollments
      cy.get('[data-cy="memberActivitiesTable"] tbody tr')
      .eq(0)
      .find('[data-cy="showEnrollments"]')
      .click();
      
      // Assert there is 1 enrollment with the correct motivation
      cy.get('[data-cy="activityEnrollmentsTable"] tbody tr')
        .eq(0)
        .children()
        .eq(0)
        .should('contain', MOTIVATION);

      cy.logout();
      cy.deleteAllButArs();

    });
  });
  