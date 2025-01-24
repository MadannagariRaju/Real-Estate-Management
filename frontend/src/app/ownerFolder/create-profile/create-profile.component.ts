import { HttpHeaders } from '@angular/common/http';
import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-create-profile',
  templateUrl: './create-profile.component.html',
  styleUrls: ['./create-profile.component.css']
})
export class CreateProfileComponent {
  profile = {
    fullName: '',
    email: '',
    phno: null,
    address: ''
  };
  isFirstTimeLogin: boolean = false;

  constructor(private http: HttpClient, private router: Router, private toastr: ToastrService) {}

  ngOnInit(): void {
    this.getUserDetails()
      .then(userDetails => {
        if (userDetails) {
          this.profile.fullName = userDetails.fullName || '';
          this.profile.email = userDetails.email || '';
          this.profile.phno = userDetails.phno || '';
          this.profile.address = userDetails.address || '';
        } else {
          console.warn('No user details found.');
          // Handle cases where userDetails is null
        }
      })
      .catch((error: any) => {
        console.error('Error fetching profile data:', error);
      });
  }
  

  getUserDetails(): Promise<any> {
    const url = 'http://localhost:8080/profile/getprofile';
    return fetch(url, {
      method: 'GET',
      headers: { 'Content-Type': 'application/json' },
      credentials: 'include'
    })
      .then(response => {
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }
        return response.json();
      })
      .catch(error => {
        console.error('Error fetching user details:', error);
        return null; // Return null to ensure consistent handling
      });
  }
  

  saveProfile(): void {
    const url = 'http://localhost:8080/profile/create';
    this.http.put(url, this.profile, {
      withCredentials: true,
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      })
    }).subscribe({
      next: (response: any) => {
        console.log('Profile saved successfully:', response);
        // alert('Profile saved successfully!');
        // alert('Message sent successfully!');
        this.toastr.success('Profile Saved successfully !', 'Success', {
          timeOut: 5000,
          progressBar: true
        });
        // this.router.navigate(['/create-profile']); // Redirect to dashboard or another page
        this.getUserDetails()
      },
      error: (error: any) => {
        this.toastr.error('error occured on saving profile!', 'error', {
          timeOut: 5000,
          progressBar: true
        });
        console.error('Error saving profile:', error);
        alert('Failed to save profile. Please try again.');
      }
    });
  }
}
