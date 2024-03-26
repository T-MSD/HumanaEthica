describe('Volunteer', () => {
  beforeEach(() => {
    //cy.deleteAllbutArs();
    //cy.createInstitutions();
    cy.demoMemberLogin()
  });

  afterEach(() => {
    cy.logout();
    cy.deleteAllButArs();
  });

  it ('check activities', () => {
    cy.get('[data-cy="institution"]').click();
    cy.get('[data-cy="assessments"]').click();
    cy.get('[data-cy="institutionAssessmentsTable"] tbody tr')
        .should('have.length', 1);
    cy.get('[data-cy="institutionAssessmentsTable"] tbody tr').eq(0).children().eq(0).should('contain', 'thisReviewHas10Characters');

  });


  it('close', () => {
    cy.get('[data-cy="institution"]').click();
    cy.get('[data-cy="members"]').click();

    cy.get('[data-cy="institution"]').click();
    cy.get('[data-cy="themes"]').click();

    cy.get('[data-cy="institution"]').click();
    cy.get('[data-cy="activities"]').click();

  });
});
