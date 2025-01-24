import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent {



    constructor(private http:HttpClient, private router:Router){}

  navigateTo(route: string): void {
    this.router.navigate([`/${route}`]);
  }

  logout() {

    console.log("customer logout")

    fetch('http://localhost:8080/api/logout', {
      method: 'POST',
      credentials: 'include',
      headers: {
        'Content-Type': 'application/json'
      }
    })
      .then(response => {
        if (response.ok) {
          console.log('Logout successful');
          this.router.navigate(['/']);
        } else {
          console.error('Logout failed');
        }
      })
      .catch(error => {
        console.error('Error during logout:', error);
      });
  }

}
