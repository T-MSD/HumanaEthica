describe('Volunteer', () => {
  beforeEach(() => {
    cy.deleteAllbutArs();
    cy.createInstitutions();
    cy.demoVolunteerLogin();

  });

  afterEach(() => {
    cy.logout();
    cy.deleteAllButArs();
  });

  it('check activities', () => {
    cy.get('[data-cy="activities"]').click();
    cy.get('[data-cy="volunteerActivitiesTable"] tbody tr')
        .should('have.length', 6);
    cy.get('[data-cy="volunteerActivitiesTable"] tbody tr')
        .eq(0).children().eq(5).should('contain', 'A1');
    cy.get('[data-cy="volunteerActivitiesTable"] tbody tr')
        .eq(0).find('[data-cy="writeAssessmentButton"]').click();
    cy.get('[data-cy="descriptionInput"]').type('thisReviewHas10Characters');
    cy.get('[data-cy="saveAssessment"]').click();
    cy.get('[data-cy="volunteerActivitiesTable"] tbody tr').eq(0).children().eq(3).should('contain', 'thisReviewHas10Characters');

  });
});
