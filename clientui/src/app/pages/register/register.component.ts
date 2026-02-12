import { Component, DestroyRef, inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { User } from '../../core/models/User';
import { CommonModule } from '@angular/common';
import { UserService } from '../../core/services/user.service';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

@Component({
  selector: 'app-register',
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './register.component.html',
  standalone: true,
  styleUrl: './register.component.scss'
})
export class RegisterComponent implements OnInit {
  constructor(private router: Router) { }

  private userService = inject(UserService);
  private formBuilder = inject(FormBuilder);
  private destroyRef = inject(DestroyRef);
  registerForm!: FormGroup;
  registerError: boolean = false;
  submitted: boolean = false;

  ngOnInit() {
    this.registerForm = this.formBuilder.group({
        email: ['', [Validators.required, Validators.email]],
        password: ['', [Validators.required, Validators.pattern("^[a-zA-Z0-9 ]{8,}")]],
        passwordConfirm: ['', [Validators.required, Validators.pattern("^[a-zA-Z0-9 ]{8,}")]]
    });
  }

  get form() {
    return this.registerForm.controls;
  }

  onSubmit(): void {
    this.submitted = true;

    if (this.registerForm.invalid)
      return;

    if (this.registerForm.get('password')?.value !== this.registerForm.get('passwordConfirm')?.value)
      return;

    const registerUser: User = {
      email: this.registerForm.get('email')?.value,
      password: this.registerForm.get('password')?.value
    };

    this.userService.register(registerUser)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
          next: () => {},
          error: (err: any) => {
            this.registerError = true;
            console.error(err);
          },
          complete: () => {
            this.router.navigateByUrl('login');
          }
        });
  }

  onReset(): void {
    this.submitted = false;
    this.registerForm.reset();
  }
}
