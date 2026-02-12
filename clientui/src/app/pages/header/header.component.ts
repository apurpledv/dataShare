import { Component, OnInit } from '@angular/core';
import { Router, RouterLink } from "@angular/router";

@Component({
  selector: 'app-header',
  imports: [RouterLink],
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss'
})
export class HeaderComponent implements OnInit {
  constructor(private router: Router) { }

  ngOnInit(): void {
    var btn = document.getElementById('logoutBtn');
    if (btn == null)
      return;

    if (sessionStorage.getItem('user_id') != null)
      btn.innerText = "Déconnexion";
  }

  onLogout(): void {
    var btn = document.getElementById('logoutBtn');
    if (btn == null)
      return;

    sessionStorage.removeItem('user_id');
    sessionStorage.removeItem('auth_token');
    btn.innerText = "Se connecter";
  }
}
