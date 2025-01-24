import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-bussiness-profile',
  templateUrl: './bussiness-profile.component.html',
  styleUrls: ['./bussiness-profile.component.css']
})
export class BussinessProfileComponent implements OnInit {
  agent = {
    businessName: '',
    agentName: '',
    contact: '',
    location: '',
    specializations: '',
    description: '',
    workingHours: '',
    experience: '',
    licenseNumber: '',
    email: ''
  };

  isFirstTimeLogin: boolean = true;
  loading: boolean = false; 
  errorMessage: string = '';

  constructor(private http: HttpClient, private router: Router , private toastr: ToastrService) {}

  ngOnInit(): void {
      this.getAgentProfile();
  }

  // Fetch the agent's profile from the backend
  getAgentProfile(): void {
    this.loading = true;
    const url = 'http://localhost:8080/business/get-profile';
    if(this.isFirstTimeLogin == true) {
      this.http.get<any>(url, { withCredentials: true }).subscribe({
        next: (response: { profile: { businessName: string; agentName: string; contact: string; location: string; specializations: string; description: string; workingHours: string; experience: string; licenseNumber: string; email: string; }; }) => {
          this.loading = false;
          if (response && response.profile) {
            this.agent = response.profile; 
            this.isFirstTimeLogin = false;
          } else {
            console.warn('No profile data found.');
            this.isFirstTimeLogin = true;
          }
        },
        error: (error: any) => {
          this.loading = false;
          console.error('Error fetching profile:', error);
          this.errorMessage = 'Failed to fetch profile. Please try again later.';
        }
      });
    }
  }

  // Save or create the agent's profile
  saveAgentProfile(): void {
    this.loading = true;
    const url = 'http://localhost:8080/business/create-profile';
    this.http.put(url, this.agent, { withCredentials: true }).subscribe({
      next: (response: any) => {
        this.loading = false;
        console.log('Profile created successfully:', response);
        this.toastr.success('Profile Saved successfully !', 'Success', {
          timeOut: 5000,
          progressBar: true
        });
        this.getAgentProfile(); 
      },
      error: (error: any) => {
        this.loading = false;
        console.error('Error creating profile:', error);
        this.errorMessage = 'Failed to save profile. Please try again later.';
      }
    });
  }
}
