import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-agent',
  templateUrl: './agent.component.html',
  styleUrls: ['./agent.component.css']
})
export class AgentComponent {
  name: string = '';  
  role: string = 'agent'; 
  dropdownOpen = false; 
  
  constructor(private http: HttpClient, private router: Router) {}



  // Function to toggle the dropdown
  toggleDropdown() {
    this.dropdownOpen = !this.dropdownOpen;
  }

  ngOnInit(): void {
    this.getUserDetails();
  }

  navigateTo(page: string): void {
    console.log(`Navigating to: ${page}`);
    this.router.navigate([`/${page}`]);
  }

  getUserDetails(): void {
    const url = 'http://localhost:8080/api/username';  // Backend URL

    this.http.get(url, {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
      }),
      withCredentials: true,  // Include credentials
    }).subscribe({
      next: (response: any) => {
        console.log(response.fullName);
        this.name = this.formatName(response.fullName);
        console.log('Name:', this.name);
      },
      error: (error: any) => {
        console.error('Error fetching user details:', error);
      }
    });
  }

  // Method to format the name
  formatName(name: string): string {
    if (!name) return name; 
    return name.charAt(0).toUpperCase() + name.slice(1).toLowerCase();
  }

  logout(): void {
    fetch('http://localhost:8080/api/logout', {
      method: 'POST',
      credentials: 'include',
      headers: {
        'Content-Type': 'application/json'
      }
    })
    .then(response => {
      if (response.ok) {
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
