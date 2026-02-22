import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { LoginComponent } from "./pages/login/login.component";
import { HeaderComponent } from "./pages/header/header.component";

@Component({
  selector: 'app-root',
  imports: [RouterOutlet/*, HeaderComponent*/],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent {
  title = 'clientui';
}
