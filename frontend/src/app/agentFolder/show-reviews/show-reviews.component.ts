import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-show-reviews',
  templateUrl: './show-reviews.component.html',
  styleUrls: ['./show-reviews.component.css']
})
export class ShowReviewsComponent implements OnInit {

  reviews?: any[]; // Initialize with an empty array
  isLoading: boolean = false;
  errorMessage: string = '';

  constructor(private http: HttpClient, private router: Router) {}

  ngOnInit(): void {
    this.getReviewsOfAgent();
  }

  getReviewsOfAgent(): void {
    this.isLoading = true; // Show loader
    this.errorMessage = ''; // Reset error message

    this.http.get<any[]>(`http://localhost:8080/review/agent-reviews`, { withCredentials: true }).subscribe(
      (data: any[]) => {
        console.log('Reviews fetched:', data);
        this.reviews = data; // Ensure reviews is always an array
        this.isLoading = false;
      },
      (error: any) => {
        console.error('Error fetching reviews:', error);
        this.errorMessage = 'Failed to load reviews. Please try again later.';
        this.isLoading = false;
      }
    );
  }
}
