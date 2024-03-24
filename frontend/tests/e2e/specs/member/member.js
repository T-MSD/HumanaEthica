describe('Volunteer', () => {
  beforeEach(() => {
    cy.deleteAllButArs();
  });

  afterEach(() => {
    cy.deleteAllButArs();
  });

  it('verify participations', () => {
    cy.createParticipations();
    cy.demoMemberLogin();
    cy.get('[data-cy="institution"]').click();
    cy.get('[data-cy="activities"]').click();
    cy.get('[data-cy="memberActivitiesTable"] tbody tr')
        .should('have.length', 2);
    cy.get('[data-cy="memberActivitiesTable"] tbody tr')
        .eq(0).children().eq(3).should('contain', '1');
    cy.get('[data-cy="memberActivitiesTable"] tbody tr')
        .eq(0).find('[data-cy="showEnrollments"]').click();
    cy.get('[data-cy="activityEnrollmentsTable"] tbody tr')
        .should('have.length', 2);
    cy.get('[data-cy="memberActivitiesTable"] tbody tr')
        .eq(0).children().eq(2).should('contain', 'false');
    cy.logout();
  });

  it ('check activities', () => {

    cy.createInstitutions1();
    cy.demoVolunteerLogin();


    cy.get('[data-cy="volunteerActivities"]').click();
    cy.get('[data-cy="volunteerActivitiesTable"] tbody tr')
        .should('have.length', 6);
    cy.get('[data-cy="volunteerActivitiesTable"] tbody tr')
        .eq(0).children().eq(0).should('contain', 'A1');
    cy.get('[data-cy="volunteerActivitiesTable"] tbody tr')
        .eq(0).find('[data-cy="writeAssessmentButton"]').click();
    cy.get('[data-cy="descriptionInput"]').type('thisReviewHas10Characters');
    cy.get('[data-cy="saveAssessment"]').click();
    cy.logout();

    cy.demoMemberLogin()
    cy.get('[data-cy="institution"]').click();
    cy.get('[data-cy="assessments"]').click();
    cy.get('[data-cy="institutionAssessmentsTable"] tbody tr')
        .should('have.length', 1);
    cy.get('[data-cy="institutionAssessmentsTable"] tbody tr').eq(0).children().eq(0).should('contain', 'thisReviewHas10Characters');
    cy.logout();
  });

  it('close', () => {
    cy.createParticipations();
    cy.demoMemberLogin()
    cy.get('[data-cy="institution"]').click();
    cy.get('[data-cy="members"]').click();

    cy.get('[data-cy="institution"]').click();
    cy.get('[data-cy="themes"]').click();

    cy.get('[data-cy="institution"]').click();
    cy.get('[data-cy="activities"]').click();
    cy.logout();
  });
});
