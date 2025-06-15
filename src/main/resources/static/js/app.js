// Main App Object - Thin client that only handles UI and API calls
const app = {
  // State
  user: null,
  apiUrl: "http://localhost:8080/api",

  // Initialize app
  init() {
    const savedUser = localStorage.getItem("user");
    if (savedUser) {
      this.user = JSON.parse(savedUser);
      this.showApp();
    } else {
      this.showLoginModal();
    }
    this.setupNavigation();
  },

  // UI State Management
  showApp() {
    document.getElementById("loginModal").classList.add("hidden");
    document.getElementById("userName").textContent = this.user.name;

    if (this.user.userType === "Student") {
      const balanceEl = document.getElementById("userBalance");
      balanceEl.textContent = `Balance: $${this.user.balance.toFixed(2)}`;
      balanceEl.classList.remove("hidden");
    }

    this.navigate(window.location.hash.slice(1) || "dashboard");
  },

  showLoginModal() {
    document.getElementById("loginModal").classList.remove("hidden");
  },

  showLogin() {
    document.getElementById("signupForm").classList.add("hidden");
    document.getElementById("loginForm").classList.remove("hidden");
  },

  showSignup() {
    document.getElementById("loginForm").classList.add("hidden");
    document.getElementById("signupForm").classList.remove("hidden");
  },

  toggleTutorFields(event) {
    const tutorFields = document.getElementById("tutorFields");
    if (event.target.value === "tutor") {
      tutorFields.classList.remove("hidden");
    } else {
      tutorFields.classList.add("hidden");
    }
  },

  // Navigation
  setupNavigation() {
    window.addEventListener("hashchange", () => {
      this.navigate(window.location.hash.slice(1));
    });

    document.querySelectorAll(".nav-link").forEach((link) => {
      link.addEventListener("click", (e) => {
        document
          .querySelectorAll(".nav-link")
          .forEach((l) => l.classList.remove("active"));
        e.target.classList.add("active");
      });
    });
  },

  navigate(page) {
    const content = document.getElementById("content");
    content.innerHTML = '<div class="spinner"></div>';

    switch (page) {
      case "dashboard":
        this.loadDashboard();
        break;
      case "tutors":
        this.loadTutors();
        break;
      case "bookings":
        this.loadBookings();
        break;
      case "profile":
        this.loadProfile();
        break;
      default:
        this.loadDashboard();
    }
  },

  // Authentication
  async login(event) {
    event.preventDefault();
    const formData = new FormData(event.target);

    try {
      const response = await fetch(`${this.apiUrl}/auth/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          email: formData.get("email"),
          password: formData.get("password"),
        }),
      });

      if (response.ok) {
        const user = await response.json();
        this.user = user;
        localStorage.setItem("user", JSON.stringify(user));
        this.showApp();
        this.showToast("Login successful!");
      } else {
        const error = await response.text();
        this.showToast(error || "Login failed", "error");
      }
    } catch (error) {
      this.showToast("Connection error", "error");
    }
  },

  async signup(event) {
    event.preventDefault();
    const formData = new FormData(event.target);
    const userType = formData.get("userType");

    const endpoint =
      userType === "student" ? "/auth/signup/student" : "/auth/signup/tutor";

    const data = {
      name: formData.get("name"),
      email: formData.get("email"),
      password: formData.get("password"),
      timeZoneId: Intl.DateTimeFormat().resolvedOptions().timeZone,
    };

    if (userType === "tutor") {
      data.hourlyRate = parseFloat(formData.get("hourlyRate"));
      data.description = formData.get("description");
    }

    try {
      const response = await fetch(`${this.apiUrl}${endpoint}`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data),
      });

      if (response.ok) {
        const user = await response.json();
        this.user = user;
        localStorage.setItem("user", JSON.stringify(user));
        this.showApp();
        this.showToast("Signup successful!");
      } else {
        const error = await response.text();
        this.showToast(error || "Signup failed", "error");
      }
    } catch (error) {
      this.showToast("Connection error", "error");
    }
  },

  logout() {
    localStorage.removeItem("user");
    this.user = null;
    window.location.hash = "";
    this.showLoginModal();
  },

  // Dashboard
  async loadDashboard() {
    const endpoint =
      this.user.userType === "Student"
        ? `/dashboard/student/${this.user.id}`
        : `/dashboard/tutor/${this.user.id}`;

    try {
      const response = await fetch(`${this.apiUrl}${endpoint}`);
      const data = await response.json();

      if (this.user.userType === "Student") {
        this.renderStudentDashboard(data);
      } else {
        this.renderTutorDashboard(data);
      }
    } catch (error) {
      document.getElementById("content").innerHTML =
        "<p>Error loading dashboard</p>";
    }
  },

  renderStudentDashboard(data) {
    // Update balance in header
    this.user.balance = data.student.balance;
    localStorage.setItem("user", JSON.stringify(this.user));
    document.getElementById(
      "userBalance"
    ).textContent = `Balance: $${data.student.balance.toFixed(2)}`;

    const content = document.getElementById("content");
    content.innerHTML = `
            <h2>Welcome back, ${data.student.name}!</h2>
            
            <div class="stats-grid">
                <div class="stat-card">
                    <div class="stat-label">Account Balance</div>
                    <div class="stat-value">$${data.student.balance.toFixed(
                      2
                    )}</div>
                    <button class="btn btn-primary btn-sm" onclick="app.showAddFundsModal()">Add Funds</button>
                </div>
            </div>

            <div class="mb-4">
                <h3>Upcoming Sessions</h3>
                ${data.upcomingBookings
                  .map(
                    (booking) => `
                    <div class="card">
                        <h4>${booking.subject.name}</h4>
                        <p>Tutor: ${booking.tutorName}</p>
                        <p>Date: ${new Date(
                          booking.dateTime
                        ).toLocaleString()}</p>
                        <span class="booking-status ${booking.status.toLowerCase()}">${
                      booking.status
                    }</span>
                        ${
                          booking.status === "PENDING"
                            ? `<button class="btn btn-primary" onclick="app.confirmBooking('${booking.id}')">Confirm & Pay</button>`
                            : ""
                        }
                    </div>
                `
                  )
                  .join("")}
            </div>

            <div>
                <h3>Available Subjects</h3>
                <div class="grid grid-cols-4">
                    ${data.availableSubjects
                      .map(
                        (subject) => `
                        <div class="card">
                            <h4>${subject.name}</h4>
                            <button class="btn btn-primary" onclick="app.searchTutorsBySubject('${subject.id}')">
                                Find Tutors
                            </button>
                        </div>
                    `
                      )
                      .join("")}
                </div>
            </div>
        `;
  },

  renderTutorDashboard(data) {
    const content = document.getElementById("content");
    content.innerHTML = `
            <h2>Welcome back, ${data.tutor.name}!</h2>
            
            <div class="stats-grid">
                <div class="stat-card">
                    <div class="stat-label">Total Earnings</div>
                    <div class="stat-value">$${data.stats.totalEarnings.toFixed(
                      2
                    )}</div>
                </div>
                <div class="stat-card">
                    <div class="stat-label">Average Rating</div>
                    <div class="stat-value">⭐ ${data.stats.averageRating.toFixed(
                      1
                    )}</div>
                </div>
            </div>

            <div class="mb-4">
                <h3>Upcoming Sessions</h3>
                ${data.upcomingBookings
                  .map(
                    (booking) => `
                    <div class="card">
                        <h4>${booking.subject.name}</h4>
                        <p>Student: ${booking.studentName}</p>
                        <p>Date: ${new Date(
                          booking.dateTime
                        ).toLocaleString()}</p>
                        <span class="booking-status ${booking.status.toLowerCase()}">${
                      booking.status
                    }</span>
                        ${
                          booking.status === "CONFIRMED"
                            ? `<button class="btn btn-success" onclick="app.completeBooking('${booking.id}')">Mark Complete</button>`
                            : ""
                        }
                    </div>
                `
                  )
                  .join("")}
            </div>

            <div>
                <h3>Recent Reviews</h3>
                ${data.recentReviews
                  .map(
                    (review) => `
                    <div class="card">
                        <div class="rating">${"⭐".repeat(review.rating)}</div>
                        <p>${review.comment}</p>
                    </div>
                `
                  )
                  .join("")}
            </div>
        `;
  },

  // Find Tutors
  async loadTutors() {
    const content = document.getElementById("content");

    // Load subjects for filter
    const subjectsResponse = await fetch(`${this.apiUrl}/subjects`);
    const subjects = await subjectsResponse.json();

    content.innerHTML = `
            <h2>Find Tutors</h2>
            
            <div class="search-filters">
                <form onsubmit="app.searchTutors(event)">
                    <div class="filter-row">
                        <select name="subjectId" class="form-select">
                            <option value="">All Subjects</option>
                            ${subjects
                              .map(
                                (s) =>
                                  `<option value="${s.id}">${s.name}</option>`
                              )
                              .join("")}
                        </select>
                        <input type="number" name="minPrice" placeholder="Min Price" class="form-input">
                        <input type="number" name="maxPrice" placeholder="Max Price" class="form-input">
                        <select name="minRating" class="form-select">
                            <option value="">Any Rating</option>
                            <option value="4">4+ Stars</option>
                            <option value="3">3+ Stars</option>
                        </select>
                    </div>
                    <button type="submit" class="btn btn-primary">Search</button>
                </form>
            </div>
            
            <div id="tutorsList"></div>
        `;

    // Load all tutors initially
    this.searchTutors();
  },

  async searchTutors(event) {
    if (event) event.preventDefault();

    const params = new URLSearchParams();
    if (event) {
      const formData = new FormData(event.target);
      for (let [key, value] of formData.entries()) {
        if (value) params.append(key, value);
      }
    }

    const response = await fetch(
      `${this.apiUrl}/dashboard/search/tutors?${params}`
    );
    const tutors = await response.json();

    document.getElementById("tutorsList").innerHTML = tutors
      .map(
        (tutor) => `
            <div class="card tutor-card">
                <div class="tutor-info">
                    <h4>${tutor.name}</h4>
                    <p>$${
                      tutor.hourlyRate
                    }/hour - ⭐ ${tutor.averageRating.toFixed(1)}</p>
                    <p>${tutor.description}</p>
                    <div class="subjects-list">
                        ${tutor.subjects
                          .map(
                            (s) => `<span class="subject-tag">${s.name}</span>`
                          )
                          .join("")}
                    </div>
                    <button class="btn btn-primary" onclick="app.openBookingModal('${
                      tutor.id
                    }', '${tutor.name}', ${tutor.hourlyRate}, '${
          tutor.subjects[0]?.id
        }', '${tutor.subjects[0]?.name}')">
                        Book Session
                    </button>
                </div>
            </div>
        `
      )
      .join("");
  },

  searchTutorsBySubject(subjectId) {
    window.location.hash = "tutors";
    // Wait for page to load then search
    setTimeout(() => {
      const form = document.querySelector(".search-filters form");
      form.subjectId.value = subjectId;
      form.dispatchEvent(new Event("submit"));
    }, 100);
  },

  // Bookings
  async loadBookings() {
    const endpoint =
      this.user.userType === "Student"
        ? `/bookings/student/${this.user.id}`
        : `/bookings/tutor/${this.user.id}`;

    const response = await fetch(`${this.apiUrl}${endpoint}`);
    const bookings = await response.json();

    const content = document.getElementById("content");
    content.innerHTML = `
            <h2>My Bookings</h2>
            ${bookings
              .map(
                (booking) => `
                <div class="card">
                    <h4>${booking.subject.name}</h4>
                    <p>${
                      this.user.userType === "Student" ? "Tutor" : "Student"
                    }: ${booking.tutorId}</p>
                    <p>Date: ${new Date(booking.dateTime).toLocaleString()}</p>
                    <span class="booking-status ${booking.status.toLowerCase()}">${
                  booking.status
                }</span>
                    
                    ${
                      booking.status === "PENDING" &&
                      this.user.userType === "Student"
                        ? `<button class="btn btn-primary" onclick="app.confirmBooking('${booking.id}')">Confirm & Pay</button>`
                        : ""
                    }
                    ${
                      booking.status === "CONFIRMED" &&
                      this.user.userType === "Tutor"
                        ? `<button class="btn btn-success" onclick="app.completeBooking('${booking.id}')">Mark Complete</button>`
                        : ""
                    }
                    ${
                      booking.status === "COMPLETED" &&
                      this.user.userType === "Student"
                        ? `<button class="btn btn-primary" onclick="app.openReviewModal('${booking.tutorId}')">Leave Review</button>`
                        : ""
                    }
                    ${
                      booking.status === "PENDING" ||
                      booking.status === "CONFIRMED"
                        ? `<button class="btn btn-danger" onclick="app.cancelBooking('${booking.id}')">Cancel</button>`
                        : ""
                    }
                </div>
            `
              )
              .join("")}
        `;
  },

  // Profile
  async loadProfile() {
    const endpoint =
      this.user.userType === "Student"
        ? `/students/${this.user.id}`
        : `/tutors/${this.user.id}`;

    const response = await fetch(`${this.apiUrl}${endpoint}`);
    const profile = await response.json();

    const content = document.getElementById("content");
    content.innerHTML = `
            <h2>My Profile</h2>
            
            <div class="profile-section">
                <div class="profile-picture-container">
                    <img src="${
                      profile.profilePictureUrl || "/api/files/default-avatar"
                    }" class="profile-picture">
                    <input type="file" id="profilePic" accept="image/*" style="display:none" onchange="app.uploadProfilePicture(event)">
                    <button class="btn btn-secondary" onclick="document.getElementById('profilePic').click()">Change Picture</button>
                </div>
                
                <form onsubmit="app.updateProfile(event)">
                    <input type="text" name="name" value="${
                      profile.name
                    }" class="form-input" placeholder="Name">
                    <input type="email" name="email" value="${
                      profile.email
                    }" class="form-input" placeholder="Email">
                    <input type="password" name="password" class="form-input" placeholder="New Password (optional)">
                    <input type="text" name="timeZoneId" value="${
                      profile.timeZoneId
                    }" class="form-input" placeholder="Timezone">
                    
                    ${
                      this.user.userType === "Tutor"
                        ? `
                        <input type="number" name="hourlyRate" value="${profile.hourlyRate}" class="form-input" placeholder="Hourly Rate">
                        <textarea name="description" class="form-textarea">${profile.description}</textarea>
                    `
                        : ""
                    }
                    
                    <button type="submit" class="btn btn-primary">Update Profile</button>
                </form>
            </div>
            
            ${
              this.user.userType === "Tutor"
                ? `
                <div class="mt-4">
                    <h3>My Subjects</h3>
                    ${profile.subjects
                      .map(
                        (s) => `
                        <span class="subject-tag">${s.name} 
                            <button onclick="app.removeSubject('${s.id}')">×</button>
                        </span>
                    `
                      )
                      .join("")}
                    <button class="btn btn-secondary" onclick="app.showAddSubjectModal()">Add Subject</button>
                </div>
                
                <div class="mt-4">
                    <h3>My Availability</h3>
                    ${profile.availability
                      .map(
                        (a) => `
                        <div>${a.dayOfWeek}: ${a.startTime} - ${a.endTime}
                            <button onclick="app.removeAvailability('${a.dayOfWeek}', '${a.startTime}', '${a.endTime}')">Remove</button>
                        </div>
                    `
                      )
                      .join("")}
                    <button class="btn btn-secondary" onclick="app.showAvailabilityModal()">Add Availability</button>
                </div>
            `
                : ""
            }
        `;
  },

  // Modal Operations
  showAddFundsModal() {
    document.getElementById("addFundsModal").classList.remove("hidden");
  },

  openBookingModal(tutorId, tutorName, hourlyRate, subjectId, subjectName) {
    document.getElementById("bookingTutorId").value = tutorId;
    document.getElementById("bookingTutorName").value = tutorName;
    document.getElementById("bookingSubjectId").value = subjectId;
    document.getElementById("bookingSubjectName").value = subjectName;

    // NOTE: Total cost calculation happens here - should this be moved to backend?
    window.currentHourlyRate = hourlyRate;
    this.updateBookingCost();

    document.getElementById("bookingModal").classList.remove("hidden");
  },

  updateBookingCost() {
    // NOTE: This calculation happens in frontend - should be validated in backend
    const hours = document.querySelector('[name="durationHours"]').value;
    const total = window.currentHourlyRate * hours;
    document.getElementById("bookingTotalCost").textContent = `$${total.toFixed(
      2
    )}`;
  },

  openReviewModal(tutorId) {
    document.getElementById("reviewTutorId").value = tutorId;
    document.getElementById("reviewModal").classList.remove("hidden");
  },

  showAvailabilityModal() {
    document.getElementById("availabilityModal").classList.remove("hidden");
  },

  showAddSubjectModal() {
    // NOTE: Need to implement subject selection modal
    alert("Add subject modal not implemented yet");
  },

  closeModal(modalId) {
    document.getElementById(modalId).classList.add("hidden");
  },

  // API Actions
  async addFunds(event) {
    event.preventDefault();
    const amount = event.target.amount.value;

    const response = await fetch(
      `${this.apiUrl}/students/${this.user.id}/add-funds`,
      {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ amount: parseFloat(amount) }),
      }
    );

    if (response.ok) {
      const result = await response.json();
      this.user.balance = result.data; // Assuming SimpleDataResponse
      localStorage.setItem("user", JSON.stringify(this.user));
      this.closeModal("addFundsModal");
      this.showToast("Funds added successfully!");
      this.loadDashboard();
    }
  },

  async createBooking(event) {
    event.preventDefault();
    const formData = new FormData(event.target);

    const response = await fetch(`${this.apiUrl}/bookings`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        studentId: this.user.id,
        tutorId: formData.get("tutorId"),
        subjectId: formData.get("subjectId"),
        dateTime: formData.get("dateTime"),
        durationHours: parseInt(formData.get("durationHours")),
      }),
    });

    if (response.ok) {
      this.closeModal("bookingModal");
      this.showToast("Booking created!");
      this.loadBookings();
    } else {
      const error = await response.text();
      this.showToast(error, "error");
    }
  },

  async confirmBooking(bookingId) {
    if (!confirm("Confirm and pay for this booking?")) return;

    const response = await fetch(
      `${this.apiUrl}/bookings/${bookingId}/confirm`,
      {
        method: "POST",
      }
    );

    if (response.ok) {
      this.showToast("Booking confirmed!");
      this.loadDashboard();
    }
  },

  async cancelBooking(bookingId) {
    if (!confirm("Cancel this booking?")) return;

    const response = await fetch(
      `${this.apiUrl}/bookings/${bookingId}/cancel`,
      {
        method: "POST",
      }
    );

    if (response.ok) {
      this.showToast("Booking cancelled");
      this.loadBookings();
    }
  },

  async completeBooking(bookingId) {
    const response = await fetch(
      `${this.apiUrl}/bookings/${bookingId}/complete`,
      {
        method: "POST",
      }
    );

    if (response.ok) {
      this.showToast("Booking completed!");
      this.loadDashboard();
    }
  },

  async submitReview(event) {
    event.preventDefault();
    const formData = new FormData(event.target);

    const response = await fetch(`${this.apiUrl}/reviews`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        studentId: this.user.id,
        tutorId: formData.get("tutorId"),
        rating: parseInt(formData.get("rating")),
        comment: formData.get("comment"),
      }),
    });

    if (response.ok) {
      this.closeModal("reviewModal");
      this.showToast("Review submitted!");
    }
  },

  async updateProfile(event) {
    event.preventDefault();
    const formData = new FormData(event.target);

    const endpoint =
      this.user.userType === "Student"
        ? `/students/${this.user.id}`
        : `/tutors/${this.user.id}`;

    const data = {};
    for (let [key, value] of formData.entries()) {
      if (value) data[key] = value;
    }

    const response = await fetch(`${this.apiUrl}${endpoint}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(data),
    });

    if (response.ok) {
      this.showToast("Profile updated!");
      this.loadProfile();
    }
  },

  async uploadProfilePicture(event) {
    const file = event.target.files[0];
    if (!file) return;

    const formData = new FormData();
    formData.append("file", file);

    const endpoint =
      this.user.userType === "Student"
        ? `/students/${this.user.id}/profile-picture`
        : `/tutors/${this.user.id}/profile-picture`;

    const response = await fetch(`${this.apiUrl}${endpoint}`, {
      method: "POST",
      body: formData,
    });

    if (response.ok) {
      this.showToast("Profile picture updated!");
      this.loadProfile();
    }
  },

  async addAvailability(event) {
    event.preventDefault();
    const formData = new FormData(event.target);

    const response = await fetch(
      `${this.apiUrl}/tutors/${this.user.id}/availability`,
      {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          dayOfWeek: formData.get("dayOfWeek"),
          startTime: formData.get("startTime"),
          endTime: formData.get("endTime"),
        }),
      }
    );

    if (response.ok) {
      this.closeModal("availabilityModal");
      this.showToast("Availability added!");
      this.loadProfile();
    }
  },

  async removeAvailability(day, start, end) {
    const response = await fetch(
      `${this.apiUrl}/tutors/${this.user.id}/availability`,
      {
        method: "DELETE",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          dayOfWeek: day,
          startTime: start,
          endTime: end,
        }),
      }
    );

    if (response.ok) {
      this.showToast("Availability removed");
      this.loadProfile();
    }
  },

  async removeSubject(subjectId) {
    const response = await fetch(
      `${this.apiUrl}/tutors/${this.user.id}/subjects/${subjectId}`,
      {
        method: "DELETE",
      }
    );

    if (response.ok) {
      this.showToast("Subject removed");
      this.loadProfile();
    }
  },

  // Utility
  showToast(message, type = "success") {
    const toast = document.getElementById("toast");
    toast.textContent = message;
    toast.className = `toast show ${type}`;

    setTimeout(() => {
      toast.classList.remove("show");
    }, 3000);
  },
};

// Initialize when DOM is ready
document.addEventListener("DOMContentLoaded", () => {
  app.init();
});

/*
NOTES FOR BACKEND IMPLEMENTATION:

1. Missing Endpoints/Features:
   - GET /api/tutors/{id} - Need endpoint to get single tutor with subjects for booking modal
   - POST /api/tutors/{id}/subjects/{subjectId} - Add subject to tutor (exists but not used in UI)
   - Need way to get available subjects that tutor doesn't have yet

2. Frontend Calculations (should these be backend?):
   - Booking total cost calculation (line 348-352)
   - These should probably be validated/recalculated on backend for security

3. Response Format Assumptions:
   - Assuming SimpleDataResponse returns { data: value } format
   - Assuming error responses are plain text (might need JSON parsing)

4. Missing UI Features:
   - Add subject modal for tutors
   - Timezone selection in profile (currently just shows current timezone)
   - Search by tutor name/text
   - Pagination for lists

5. Data that needs enrichment:
   - Booking responses show tutorId/studentId but UI needs names
   - DashboardController already provides enriched data, but regular BookingController doesn't
*/
