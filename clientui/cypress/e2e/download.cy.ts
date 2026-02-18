describe('File Upload', () => {
  const validUser = {
    email: 'defaultUser',
    password: 'defaultUser'
  };
  
  it('downloads a file', () => {
    cy.login(validUser.email, validUser.password);

    cy.uploadTestFile();

    cy.get('.viewFileBtn').last().click();
    cy.url({ timeout: 5000 }).should('include', '/download');

    cy.get('#downloadFileBtn').click();
    cy.readFile('cypress/downloads/exampleFile.txt').should('exist');

    cy.get('.returnBtn').first().click();
    cy.url({ timeout: 5000 }).should('include', '/dashboard');
    
    cy.get('.deleteFileBtn').last().click();
  });
});