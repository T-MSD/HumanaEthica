describe('Volunteer', () => {
  beforeEach(() => {
    cy.deleteAllButArs();
    cy.createParticipations();
    cy.demoMemberLogin()
  });

  afterEach(() => {
    cy.logout();
    cy.deleteAllButArs();
  });

  it('verify participations', () => {
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
    cy.get('[data-cy="activityEnrollmentsTable"] tbody tr')
        .eq(0).children().eq(2).should('contain', 'false');
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
