import { ChangeDetectorRef, Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ToastrService } from 'ngx-toastr';
import { Router } from '@angular/router';

@Component({
  selector: 'app-add-property',
  templateUrl: './add-property.component.html',
  styleUrls: ['./add-property.component.css'],
})
export class AddPropertyComponent {
  form = {
    title: '',
    description: '',
    location: '', // Bound to the input box
    propertyType: '',
    price: '',
    contact: '',
    amenities: '',
    latitude: 0, // Add latitude
    longitude: 0 // Add longitude
  };
  
  constructor(private http: HttpClient, private toastr: ToastrService, private router: Router, private cdr: ChangeDetectorRef) {}


  locationError: string | null = null;
  map: any;
  marker: any;
  

  ngOnInit() {

    console.log("ngOnInit is called");
    (window as any)['initMap'] = this.initializeMap.bind(this);
  
    setTimeout(() => {
      this.initializeMap();
    }, 1000);
  }  

  initializeMap() {
    this.map = new google.maps.Map(document.getElementById('map') as HTMLElement, {
      center: { lat: 17.385044, lng: 78.486671 }, // Centered on Telangana
      zoom: 7, // Adjust zoom level as needed
    });

    // Add a click listener to the map
    this.map.addListener('click', (event: any) => {
      const lat = event.latLng.lat();
      const lng = event.latLng.lng();
      this.setMarker(lat, lng);

      // Reverse geocode the clicked position
      this.reverseGeocode(lat, lng);
    });
  }

  reverseGeocode(lat: number, lng: number) {
    const geocoder = new google.maps.Geocoder();
    const latLng = new google.maps.LatLng(lat, lng);
  
    // Perform the reverse geocoding
    geocoder.geocode({ location: latLng }, (results, status) => {
      if (status === 'OK' && results && results[0]) {
        // Update form.location with the formatted address
        this.form.location = results[0].formatted_address;
        console.log("updated location: ", this.form.location)
        console.log("form: ",this.form)
        // Trigger change detection to update the input box
        this.cdr.detectChanges();
        this.locationError = null;
      } else {
        this.locationError = 'Could not retrieve the address for this location.';
      }
    });
  }

  geocodeAddress() {
    const geocoder = new google.maps.Geocoder();
    geocoder.geocode({ address: this.form.location }, (results, status) => {
      if (status === 'OK' && results && results[0]) {
        const location = results[0].geometry.location;
        this.map.setCenter(location);
        this.setMarker(location.lat(), location.lng());
        this.locationError = null;
      } else {
        this.locationError = 'Invalid address. Please select a location on the map.';
      }
    });
  }

  setMarker(lat: number, lng: number): void {
    if (this.marker) {
      this.marker.setMap(null);
    }
    this.marker = new google.maps.Marker({
      position: { lat, lng },
      map: this.map,
      draggable: true,
    });
  
    console.log("latitude: ", lat)
    console.log("longitude: ", lng)
  
    // Update form with latitude and longitude
    this.form.latitude = lat;
    this.form.longitude = lng;
  
    // Update form location when marker is moved
    this.marker.addListener('dragend', () => {
      const newPosition = this.marker.getPosition();
      this.form.latitude = newPosition.lat();
      this.form.longitude = newPosition.lng();
    });
  }
  

  selectedImage: File | null = null;
  selectedVideo: File | null = null;
  selectedFloorPlan: File | null = null;
  private apiUrl = 'http://localhost:8080/prop/addproperties';


  onImageSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input?.files && input.files.length > 0) {
      this.selectedImage = input.files[0];
      console.log('Selected image:', this.selectedImage.name);
    }
  }

  onVideoSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input?.files && input.files.length > 0) {
      this.selectedVideo = input.files[0];
      console.log('Selected video:', this.selectedVideo.name);
    }
  }

  onFloorPlanSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input?.files && input.files.length > 0) {
      this.selectedFloorPlan = input.files[0];
      console.log('Selected floor plan:', this.selectedFloorPlan.name);
    }
  }

onSubmit(): void {
  if (!this.form.title) alert("Please enter title");
  if (!this.form.amenities) alert("Please enter amenities");
  if (!this.form.contact) alert("Please enter contact number");
  if (!this.form.location) alert("Please enter location");
  if (!this.form.price) alert("Please enter price");
  if (!this.form.propertyType) alert("Please enter property type");

  if (!this.selectedImage || !this.selectedVideo) {
    alert('Please select an image and a video before submitting.');
    return;
  }

  const formData = new FormData();
  formData.append('propertyDto', new Blob([JSON.stringify(this.form)], { type: 'application/json' }));
  formData.append('image', this.selectedImage);
  formData.append('video', this.selectedVideo);

  if (this.selectedFloorPlan) {
    formData.append('floorPlan', this.selectedFloorPlan);
  }

  this.http.post(this.apiUrl, formData, {
    withCredentials: true, // Include credentials (cookies, session tokens, etc.)
  }).subscribe({
    next: (response: any) => {
      console.log('Property added successfully:', response);
      this.resetForm();
      this.router.navigate(['/owner-dashboard']);
      this.toastr.success('Property added successfully!', 'Success', { timeOut: 5000, progressBar: true });
    },
    error: (error: any) => {
      this.toastr.error('Error on adding Property!', 'Error', { timeOut: 5000, progressBar: true });
      console.error('Error adding property:', error);
    },
  });
}


  resetForm(): void {
    this.form = {
      title: '',
      description: '',
      location: '',
      propertyType: '',
      price: '',
      contact: '',
      amenities: '',
      longitude:0,
      latitude:0
    };
    this.selectedImage = null;
    this.selectedVideo = null;
    this.selectedFloorPlan = null;
  }
}
