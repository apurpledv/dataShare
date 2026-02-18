/// <reference types="cypress" />

declare namespace Cypress {
  interface Chainable<Subject = any> {
    /**
     * Logs in a user via the login page
     */
    login(email: string, password: string): Chainable<void>;
    uploadTestFile(): Chainable<void>;
  }
}