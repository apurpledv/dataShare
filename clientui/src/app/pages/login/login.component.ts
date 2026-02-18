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
  templateUrl: './login.component.html',
  standalone: true,
  styleUrl: './login.component.scss'
})
export class LoginComponent implements OnInit {
  constructor(private router: Router) { }

  private userService = inject(UserService);
  private formBuilder = inject(FormBuilder);
  private destroyRef = inject(DestroyRef);
  loginForm!: FormGroup;
  submitted: boolean = false;
  loginError: boolean = false;

  ngOnInit() {
    this.loginForm = this.formBuilder.group({
        email: ['', Validators.required],
        password: ['', Validators.required]
    });
  }

  get form() {
    return this.loginForm.controls;
  }

  onSubmit(): void {
    this.submitted = true;

    if (this.loginForm.invalid)
      return;

    const loginUser: User = {
      email: this.loginForm.get('email')?.value,
      password: this.loginForm.get('password')?.value
    };

    this.userService.login(loginUser)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
          next: (res) => {
            sessionStorage.setItem('auth_token', res.token);
            sessionStorage.setItem('user_id', res.userId);
            var btn = document.getElementById('logoutBtn');
            if (btn != null) {
              btn.innerText = "Déconnexion";
            }
          },
          error: (err: any) => {
            this.loginError = true;
            console.error(err);
          },
          complete: () => {
            this.router.navigateByUrl('dashboard');
          }
        });
  }

  onReset(): void {
    this.submitted = false;
    this.loginForm.reset();
  }
}
