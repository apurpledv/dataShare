describe('File Upload', () => {
  const validUser = {
    email: 'defaultUser',
    password: 'defaultUser'
  };
  
  it('uploads a file successfully', () => {
    cy.login(validUser.email, validUser.password);

    cy.uploadTestFile();

    cy.get('.deleteFileBtn').last().click();
  });
});