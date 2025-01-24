import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-owner-navbar',
  templateUrl: './owner-navbar.component.html',
  styleUrls: ['./owner-navbar.component.css']
})
export class OwnerNavbarComponent {

  name?:String

  ngOnInit(): void {
    this.getUserDetails();
  }

  constructor(private http: HttpClient, private router: Router) {}

  dropdownOpen = false; 
  
  role = 'Owner'; // Replace with actual user role

  // Function to toggle the dropdown
  toggleDropdown() {
    this.dropdownOpen = !this.dropdownOpen;
  }
  
  getUserDetails(): void {
    const url = 'http://localhost:8080/api/username'; // Backend URL
    this.http.get(url, {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
      }),
      withCredentials: true, // Include credentials (cookies, session tokens, etc.)
    }).subscribe({
      next: (response: any) => {
        this.name = this.formatName(response.fullName);
        console.log('Name:', this.name);
        
      },
      error: (error: any) => {
        console.error('Error fetching properties:', error);
      }
    });
  }
    // Method to format the name
    formatName(name: string): string {
      if (!name) return name; 
      return name.charAt(0).toUpperCase() + name.slice(1).toLowerCase();
    }

    logout() {
      this.http.post('http://localhost:8080/api/logout', {}, {
        headers: new HttpHeaders({
          'Content-Type': 'application/json',
        }),
        withCredentials: true // Include cookies and credentials in the request
      }).subscribe({
        next: (response: any) => {
          console.log('Logout successful');
          this.router.navigate(['/']);
        },
        error: (error: any) => {
          console.error('Logout failed', error);
        }
      });
    }
}
