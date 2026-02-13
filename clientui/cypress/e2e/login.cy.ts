describe('Login E2E', () => {
  const validUser = {
    email: 'defaultUser',
    password: 'defaultUser'
  };

  const invalidUser = {
    email: 'NotAUser',
    password: 'NotAUser'
  };

  beforeEach(() => {
    // Clear sessionStorage before each test
    cy.clearLocalStorage();
    sessionStorage.clear();
  });

  it('logs in successfully using cy.login()', () => {
    cy.login(validUser.email, validUser.password);

    // Optional: check logout button
    cy.get('#logoutBtn').should('have.text', 'Déconnexion');
  });

  it('shows error message on failed login', () => {
    cy.visit('/login');

    cy.get('input[name="email"]').type(invalidUser.email);
    cy.get('input[name="password"]').type(invalidUser.password);

    cy.get('input[type="submit"]').click();

    // Wait a bit for the backend response
    cy.get('body').should('contain.text', 'Email et/ou mot de passe incorrect');

    // Optional: ensure sessionStorage is empty
    cy.window().then((win) => {
      expect(win.sessionStorage.getItem('auth_token')).to.be.null;
      expect(win.sessionStorage.getItem('user_id')).to.be.null;
    });
  });
});

