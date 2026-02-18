import { ComponentFixture, TestBed } from "@angular/core/testing";
import { FormBuilder, FormGroup } from "@angular/forms";
import { ActivatedRoute } from "@angular/router";
import { AuthToken } from "@app/core/models/AuthToken";
import { UserService } from "@app/core/services/user.service";
import { LoginComponent } from "@app/pages/login/login.component";
import { MockUserService } from "../mock/MockUserService";
import { throwError } from "rxjs";

describe('Login IT Tests', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;

  let userService: MockUserService;
  let formBuilder: FormBuilder;

  beforeEach(async () => {
    TestBed.configureTestingModule({
        providers: [
            { provide: UserService, useClass: MockUserService },
            { provide: ActivatedRoute, useValue: {} }
        ],
    });

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    
    userService = TestBed.inject(UserService) as MockUserService;
    formBuilder = TestBed.inject(FormBuilder);
  });

  it('should submit the form and call login() from its userService', () => {
    const formThingy: FormGroup = formBuilder.group({email: ['email'], password: ['password']});
    component.loginForm = formThingy;

    // Mock a token response
    const mockToken: AuthToken = {
      token: '123456.123456.123456789',
      userId: '1'
    }
    const spyLogin = jest.spyOn(userService, 'login');
    
    component.onSubmit();

    fixture.detectChanges();
    expect(spyLogin).toHaveBeenCalled();
    expect(sessionStorage.getItem('auth_token')).toBe(mockToken.token);
    expect(sessionStorage.getItem('user_id')).toBe(mockToken.userId);
  });

  it('should submit an invalid form and NOT call login() from its userService', () => {
    const spyLogin = jest.spyOn(userService, 'login');
    
    component.onSubmit();
    expect(spyLogin).not.toHaveBeenCalled();
  });

  it('should submit a form but receive an error', () => {
    const consoleErrorMock = jest.spyOn(console, 'error').mockImplementation();
    const formThingy: FormGroup = formBuilder.group({email: ['email'], password: ['password']});
    component.loginForm = formThingy;

    const spyLogin = jest.spyOn(userService, 'login');
    spyLogin.mockImplementation(() => throwError(() => new Error('error')));
    
    component.onSubmit();
    expect(component.loginError).toBe(true);

    consoleErrorMock.mockRestore();
  });

  it('should reset the form, setting email and password to null', () => {
    const formThingy: FormGroup = formBuilder.group({email: ['email'], password: ['password']});
    component.loginForm = formThingy;
    
    component.onReset();
    
    expect(component.loginForm.controls['email'].value).toBeNull();
    expect(component.loginForm.controls['password'].value).toBeNull();
  });
});