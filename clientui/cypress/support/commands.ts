/// <reference types="cypress" />
// ***********************************************
// This example commands.ts shows you how to
// create various custom commands and overwrite
// existing commands.
//
// For more comprehensive examples of custom
// commands please read more here:
// https://on.cypress.io/custom-commands
// ***********************************************
//
//
// -- This is a parent command --
// Cypress.Commands.add('login', (email, password) => { ... })
//
//
// -- This is a child command --
// Cypress.Commands.add('drag', { prevSubject: 'element'}, (subject, options) => { ... })
//
//
// -- This is a dual command --
// Cypress.Commands.add('dismiss', { prevSubject: 'optional'}, (subject, options) => { ... })
//
//
// -- This will overwrite an existing command --
// Cypress.Commands.overwrite('visit', (originalFn, url, options) => { ... })
//
// declare global {
//   namespace Cypress {
//     interface Chainable {
//       login(email: string, password: string): Chainable<void>
//       drag(subject: string, options?: Partial<TypeOptions>): Chainable<Element>
//       dismiss(subject: string, options?: Partial<TypeOptions>): Chainable<Element>
//       visit(originalFn: CommandOriginalFn, url: string, options: Partial<VisitOptions>): Chainable<Element>
//     }
//   }
// }

// Logs in a user via the login page
Cypress.Commands.add('login', (email: string, password: string) => {
  cy.visit('/login');

  cy.get('input[name="email"]').type(email);
  cy.get('input[name="password"]').type(password);

  cy.get('input[type="submit"]').click();

  // Wait for redirect to dashboard
  cy.url({ timeout: 5000 }).should('include', '/dashboard');

  // Optional: assert sessionStorage is set
  cy.window().then((win) => {
    expect(win.sessionStorage.getItem('auth_token')).to.exist;
    expect(win.sessionStorage.getItem('user_id')).to.exist;
  });
});

// Uploads an example file
Cypress.Commands.add('uploadTestFile', () => {
  cy.visit('/upload');

  const fileName = 'cypress/fixtures/exampleFile.txt';
  cy.get('input[type="file"]').selectFile(fileName);
  cy.get('input[type="submit"]').click();

  cy.url({ timeout: 5000 }).should('include', '/dashboard');
});
