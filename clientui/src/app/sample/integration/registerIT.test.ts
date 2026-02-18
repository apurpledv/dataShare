import { ComponentFixture, TestBed } from "@angular/core/testing";
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { ActivatedRoute } from "@angular/router";
import { UserService } from "@app/core/services/user.service";
import { MockUserService } from "../mock/MockUserService";
import { throwError } from "rxjs";
import { RegisterComponent } from "@app/pages/register/register.component";

describe('Register IT Tests', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;

  let userService: MockUserService;
  let formBuilder: FormBuilder;

  beforeEach(async () => {
    TestBed.configureTestingModule({
        providers: [
            { provide: UserService, useClass: MockUserService },
            { provide: ActivatedRoute, useValue: {} }
        ],
    });

    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    
    userService = TestBed.inject(UserService) as MockUserService;
    formBuilder = TestBed.inject(FormBuilder);
  });

  it('should submit the form and call register() from its userService', () => {
    const formThingy: FormGroup = formBuilder.group({
      email: ['email@gmail.com'], 
      password: ['password'], 
      passwordConfirm: ['password']
    });
    component.registerForm = formThingy;

    const spyRegister = jest.spyOn(userService, 'register');
    
    component.onSubmit();

    fixture.detectChanges();
    expect(spyRegister).toHaveBeenCalled();
  });

  it('should submit an empty form and NOT call register() from its userService', () => {
    const formThingy: FormGroup = formBuilder.group({
      email: ['', Validators.required], 
      password: ['', Validators.required], 
      passwordConfirm: ['', Validators.required]
    });
    
    const spyRegister = jest.spyOn(userService, 'register');
    
    component.onSubmit();
    expect(spyRegister).not.toHaveBeenCalled();
  });

  it('should submit a form where the two passwords don\'t match and NOT call register() from its userService', () => {
    const formThingy: FormGroup = formBuilder.group({
      email: ['email@gmail.com'], 
      password: ['password'], 
      passwordConfirm: ['notPassword']
    });
    component.registerForm = formThingy;

    const spyRegister = jest.spyOn(userService, 'register');
    
    component.onSubmit();
    expect(spyRegister).not.toHaveBeenCalled();
  });

  it('should submit an invalid form and NOT call register() from its userService', () => {
    const formThingy: FormGroup = formBuilder.group({
      email: ['badEmail', [Validators.required, Validators.email]],
      password: ['badPW', [Validators.required, Validators.pattern("^[a-zA-Z0-9 ]{8,}")]],
      passwordConfirm: ['badPW', [Validators.required, Validators.pattern("^[a-zA-Z0-9 ]{8,}")]]
    });
    component.registerForm = formThingy;

    const spyRegister = jest.spyOn(userService, 'register');
    
    component.onSubmit();
    expect(spyRegister).not.toHaveBeenCalled();
  });

  it('should submit a form but receive an error', () => {
    const consoleWarnMock = jest.spyOn(console, 'error').mockImplementation();
    const formThingy: FormGroup = formBuilder.group({
      email: ['email@gmail.com'], 
      password: ['password'], 
      passwordConfirm: ['password']
    });
    component.registerForm = formThingy;

    const spyRegister = jest.spyOn(userService, 'register');
    spyRegister.mockImplementation(() => throwError(() => new Error('error')));
    
    component.onSubmit();
    expect(component.registerError).toBe(true);

    consoleWarnMock.mockRestore();
  });

  it('should reset the form, setting email and password to null', () => {
    const formThingy: FormGroup = formBuilder.group({
      email: ['email@gmail.com'], 
      password: ['password'], 
      passwordConfirm: ['password']
    });
    component.registerForm = formThingy;
    
    component.onReset();
    
    expect(component.registerForm.controls['email'].value).toBeNull();
    expect(component.registerForm.controls['password'].value).toBeNull();
    expect(component.registerForm.controls['passwordConfirm'].value).toBeNull();
  });
});