describe('Enrollment', () => {
  
  
    it('as a member, verify activity table has 3 instances and first activity has 0 applications', () => {
      cy.deleteAllButArs();
      cy.createDemoEntitiesTest1();
      cy.demoMemberLogin();

      // go to show activities form
      cy.intercept('GET', '/users/*/getInstitution').as('getInstitutions');

      cy.get('[data-cy="institution"]').click();
      cy.get('[data-cy="activities"]').click();

      cy.wait('@getInstitutions');

      // Assert there are 3 activities
      cy.get('[data-cy="memberActivitiesTable"] tbody tr')
      .should('have.length', 3)

      // Assert the first activity has 0 applications
      cy.get('[data-cy="memberActivitiesTable"] tbody tr')
        .eq(0)
        .children()
        .eq(4).should('contain', '0');

      cy.logout();
    });
    
  
    it('as a volunteer, apply for the first activity', () => {
      cy.demoVolunteerLogin();
      const MOTIVATION = 'Demo-Motivation';

      cy.intercept('POST', '/activities/*/enrollments').as('enroll');
      cy.intercept('GET', '/activities').as('getActivities');

      // go to show activities form
      cy.get('[data-cy="volunteerActivities"]').click();

      cy.wait('@getActivities');
  
      // Apply for the first activity
      cy.get('[data-cy="volunteerActivitiesTable"] tbody tr')
        .eq(0)
        .find('[data-cy="newEnrollment"]')
        .click();

      // fill form
      cy.get('[data-cy="motivationInput"]').type(MOTIVATION);

      // save form
      cy.get('[data-cy="saveEnrollment"]').click()

      cy.wait('@enroll');

      cy.logout();
    });
    
  
    it('as a member, verify the first activity has 1 application and check enrollments', () => {
      cy.demoMemberLogin();
      const MOTIVATION = 'Demo-Motivation';

      cy.intercept('GET', '/users/*/getInstitution').as('getInstitutions');
      cy.intercept('GET', '/activities/*/enrollments').as('getEnrollments');

      // go to show activities form
      cy.get('[data-cy="institution"]').click();
      cy.get('[data-cy="activities"]').click();

      cy.wait('@getInstitutions');

      // Assert the first activity has 1 application
      cy.get('[data-cy="memberActivitiesTable"] tbody tr')
      .eq(0).children().eq(4).should('contain', 1 );

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

      cy.wait('@getEnrollments');

      cy.logout();
      cy.deleteAllButArs();

    });
  });
  