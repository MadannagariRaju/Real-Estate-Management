import { HttpClient } from '@angular/common/http';
import { HttpHeaders } from '@angular/common/http';
import { ViewChild } from '@angular/core';
import { ElementRef } from '@angular/core';
import { AfterViewInit } from '@angular/core';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Chart } from 'chart.js';
import { IndianCurrencyFormatPipe } from "../../indian-currency-format.pipe";

@Component({
  selector: 'app-list-property',
  templateUrl: './list-property.component.html',
  styleUrls: ['./list-property.component.css'],
})
export class ListPropertyComponent implements OnInit{

  properties: any[] = [];
  selectedProperty: any = null;
  isAnalyticsModalOpen: boolean = false;
  isModalOpen: boolean = false;
  modalImageUrl: string = '';
  isEditModalOpen = false;
  editPropertyData: any = {};


  constructor(private http: HttpClient, private router: Router) {}

  ngOnInit(): void {
    this.fetchProperties();
  }

  // Open the image modal
  openImageModal(imageUrl: string): void {
    this.modalImageUrl = imageUrl;
    this.isModalOpen = true;
  }

  // Close the image modal
  closeImageModal(): void {
    this.isModalOpen = false;
  }

  // Close the edit modal
  closeEditModal() {
    this.isEditModalOpen = false;
  }

  // Save the edited property
  saveProperty() {
    const updatedPropertyIndex = this.properties.findIndex(
      (p) => p.id === this.editPropertyData.id
    );
    if (updatedPropertyIndex !== -1) {
      this.properties[updatedPropertyIndex] = { ...this.editPropertyData };
    }
    this.isEditModalOpen = false;
  }

  // Fetch properties from the backend
  fetchProperties(): void {
    const url = 'http://localhost:8080/prop/getproperties';
    this.http.get(url, {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
      }),
      withCredentials: true, // Include credentials (cookies, session tokens, etc.)
    }).subscribe({
      next: (response: any) => {
        console.log('Response from backend:', response);
        this.properties = response.map((property: any) => {
          property.imageUrl = `http://localhost:8080/prop/getPropertyImage/${property.id}`;
          property.videoUrl = `http://localhost:8080/prop/getPropertyVideo/${property.id}`;
          property.floorPlanUrl = `http://localhost:8080/prop/getPropertyFloorPlan/${property.id}`;
          return property;
        });
        console.log('Properties fetched:', this.properties);
      },
      error: (error: any) => {
        console.error('Error fetching properties:', error);
      }
    });
  }

  getAnalytics(propertyId: number): void {
    const url = `http://localhost:8080/analytics/getPropertyAnalytics/${propertyId}`; // Backend URL to fetch analytics by property ID
    this.http.get(url, {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
      }),
      withCredentials: true, // Include credentials (cookies, session tokens, etc.)
    }).subscribe({
      next: (response: any) => {
        console.log('Analytics data:', response);
        if (this.selectedProperty) {
          this.selectedProperty.analytics = response; // Add analytics data to the selected property
          
        }
      },
      error: (error: any) => {
        console.error('Error fetching analytics:', error);
      }
    });
  }

  // Open the analytics modal and fetch analytics data for the selected property
  openAnalyticsModal(property: any): void {
    this.selectedProperty = property; // Set the selected property
    this.isAnalyticsModalOpen = true; // Open the analytics modal
    this.getAnalytics(property.id); // Fetch the analytics for the selected property
  }

  // Close the analytics modal
  closeAnalyticsModal(): void {
    this.isAnalyticsModalOpen = false;
    this.selectedProperty = null; // Reset the selected property
  }

  getConversionRate(viewsCount: number, inquiriesCount: number): string {
    if (viewsCount === 0) {
      return '0'; // Avoid division by zero
    }
    return ((inquiriesCount / viewsCount) * 100).toFixed(2); // Returns percentage to two decimal places
  }
  




  // Delete property by ID
  deleteProperty(propertyId: number): void {
    const url = `http://localhost:8080/prop/deleteproperty/${propertyId}`; 

    console.log(`Attempting to delete property with ID: ${propertyId}`);

    this.http.delete(url, {
      withCredentials: true, // Include credentials (cookies, session tokens, etc.)
    }).subscribe({
      next: () => {
        console.log(`Property with ID ${propertyId} deleted successfully.`);
        this.properties = this.properties.filter(property => property.id !== propertyId); // Remove deleted property from the list
      },
      error: (error: any) => {
        console.error('Error deleting property:', error);
      }
    });
  }
}

