// Global state management
const state = {
  currentUser: null,
  currentView: "auth",
};

// API Base URL
const API_BASE_URL = "/api";

// Bootstrap modal instances
let profileEditModal;
let addFundsModal;
let createBookingModal;
let reviewModal;
let tutorDetailsModal;

// Initialize the application
document.addEventListener("DOMContentLoaded", () => {
  initializeModals();
  initializeEventListeners();
  checkAuthState();
});

// Initialize Bootstrap modals
function initializeModals() {
  profileEditModal = new bootstrap.Modal(
    document.getElementById("profileEditModal")
  );
  addFundsModal = new bootstrap.Modal(document.getElementById("addFundsModal"));
  createBookingModal = new bootstrap.Modal(
    document.getElementById("createBookingModal")
  );
  reviewModal = new bootstrap.Modal(document.getElementById("reviewModal"));
  tutorDetailsModal = new bootstrap.Modal(
    document.getElementById("tutorDetailsModal")
  );
}

// Initialize all event listeners
function initializeEventListeners() {
  // Auth listeners
  document.getElementById("loginTab").addEventListener("click", showLoginForm);
  document
    .getElementById("signupTab")
    .addEventListener("click", showSignupForm);
  document.getElementById("loginForm").addEventListener("submit", handleLogin);
  document
    .getElementById("signupForm")
    .addEventListener("submit", handleSignup);
  document
    .getElementById("signupUserType")
    .addEventListener("change", toggleTutorFields);
  document.getElementById("logoutBtn").addEventListener("click", handleLogout);

  // Student dashboard listeners
  document
    .getElementById("addFundsBtn")
    .addEventListener("click", showAddFundsModal);
  document
    .getElementById("editStudentProfileBtn")
    .addEventListener("click", showEditProfileModal);
  document
    .getElementById("findTutorBtn")
    .addEventListener("click", showSearchTutors);
  document
    .getElementById("viewStudentBookingsBtn")
    .addEventListener("click", showStudentBookings);
  document
    .getElementById("confirmAddFundsBtn")
    .addEventListener("click", handleAddFunds);

  // Tutor dashboard listeners
  document
    .getElementById("editTutorProfileBtn")
    .addEventListener("click", showEditProfileModal);
  document
    .getElementById("manageSubjectsBtn")
    .addEventListener("click", showSubjectManagement);
  document
    .getElementById("manageAvailabilityBtn")
    .addEventListener("click", showAvailabilityManagement);
  document
    .getElementById("viewTutorBookingsBtn")
    .addEventListener("click", showTutorBookings);

  // Search listeners
  document
    .getElementById("backFromSearchBtn")
    .addEventListener("click", backToDashboard);
  document
    .getElementById("searchForm")
    .addEventListener("submit", handleSearch);

  // Booking management listeners
  document
    .getElementById("backFromBookingsBtn")
    .addEventListener("click", backToDashboard);
  document
    .getElementById("upcomingTab")
    .addEventListener("click", () => showBookingTab("upcoming"));
  document
    .getElementById("pastTab")
    .addEventListener("click", () => showBookingTab("past"));
  document
    .getElementById("cancelledTab")
    .addEventListener("click", () => showBookingTab("cancelled"));

  // Subject management listeners
  document
    .getElementById("backFromSubjectsBtn")
    .addEventListener("click", backToDashboard);

  // Availability management listeners
  document
    .getElementById("backFromAvailabilityBtn")
    .addEventListener("click", backToDashboard);
  document
    .getElementById("availabilityForm")
    .addEventListener("submit", handleAddAvailability);

  // Modal listeners
  document
    .getElementById("saveProfileBtn")
    .addEventListener("click", handleUpdateProfile);
  document
    .getElementById("confirmBookingBtn")
    .addEventListener("click", handleCreateBooking);
  document
    .getElementById("submitReviewBtn")
    .addEventListener("click", handleSubmitReview);
  document
    .getElementById("bookingDuration")
    .addEventListener("input", updateBookingCost);

  // Event delegation for dynamically created content

  // Search results event delegation
  document.getElementById("searchResults").addEventListener("click", (e) => {
    if (e.target.classList.contains("book-tutor-btn")) {
      const tutorId = e.target.dataset.tutorId;
      const tutorName = e.target.dataset.tutorName;
      const hourlyRate = parseFloat(e.target.dataset.hourlyRate);
      bookTutor(tutorId, tutorName, hourlyRate);
    } else if (e.target.classList.contains("view-tutor-btn")) {
      const tutorId = e.target.dataset.tutorId;
      viewTutorDetails(tutorId);
    }
  });

  // Bookings list event delegation
  document.getElementById("bookingsList").addEventListener("click", (e) => {
    if (e.target.classList.contains("confirm-booking-btn")) {
      const bookingId = e.target.dataset.bookingId;
      confirmBooking(bookingId);
    } else if (e.target.classList.contains("cancel-booking-btn")) {
      const bookingId = e.target.dataset.bookingId;
      cancelBooking(bookingId);
    } else if (e.target.classList.contains("review-tutor-btn")) {
      const tutorId = e.target.dataset.tutorId;
      showReviewModal(tutorId);
    }
  });

  // Subject management event delegation
  document.getElementById("mySubjectsList").addEventListener("click", (e) => {
    if (e.target.classList.contains("remove-subject-btn")) {
      const subjectId = e.target.dataset.subjectId;
      removeSubject(subjectId);
    }
  });

  document
    .getElementById("availableSubjectsList")
    .addEventListener("click", (e) => {
      if (e.target.classList.contains("add-subject-btn")) {
        const subjectId = e.target.dataset.subjectId;
        addSubject(subjectId);
      }
    });

  // Availability management event delegation
  document.getElementById("availabilityList").addEventListener("click", (e) => {
    if (e.target.classList.contains("remove-availability-btn")) {
      const dayOfWeek = e.target.dataset.dayOfWeek;
      const startTime = e.target.dataset.startTime;
      const endTime = e.target.dataset.endTime;
      removeAvailability(dayOfWeek, startTime, endTime);
    }
  });
}

// Check if user is already authenticated
function checkAuthState() {
  const savedUser = localStorage.getItem("currentUser");
  if (savedUser) {
    state.currentUser = JSON.parse(savedUser);
    showDashboard();
  } else {
    showAuthView();
  }
}

// View switching functions
function showAuthView() {
  hideAllViews();
  document.getElementById("authView").style.display = "block";
  document.getElementById("mainNav").style.display = "none";
}

function showDashboard() {
  hideAllViews();
  document.getElementById("mainNav").style.display = "block";
  document.getElementById("userNameDisplay").textContent =
    state.currentUser.name;

  if (state.currentUser.userType === "Student") {
    document.getElementById(
      "balanceDisplay"
    ).textContent = `Balance: ${state.currentUser.balance}`;
    showStudentDashboard();
  } else {
    document.getElementById(
      "balanceDisplay"
    ).textContent = `Rate: ${state.currentUser.hourlyRate}/hr`;
    showTutorDashboard();
  }
}

function showStudentDashboard() {
  document.getElementById("studentDashboard").style.display = "block";
  loadStudentDashboard();
}

function showTutorDashboard() {
  document.getElementById("tutorDashboard").style.display = "block";
  loadTutorDashboard();
}

function showSearchTutors() {
  hideAllViews();
  document.getElementById("searchTutorsView").style.display = "block";
  loadSubjectsForSearch();
}

function showStudentBookings() {
  state.currentView = "studentBookings";
  showBookingManagement();
}

function showTutorBookings() {
  state.currentView = "tutorBookings";
  showBookingManagement();
}

function showBookingManagement() {
  hideAllViews();
  document.getElementById("bookingManagementView").style.display = "block";
  document.getElementById("bookingViewTitle").textContent =
    state.currentView === "studentBookings" ? "My Bookings" : "My Sessions";
  showBookingTab("upcoming");
}

function showSubjectManagement() {
  hideAllViews();
  document.getElementById("subjectManagementView").style.display = "block";
  loadTutorSubjects();
}

function showAvailabilityManagement() {
  hideAllViews();
  document.getElementById("availabilityManagementView").style.display = "block";
  loadTutorAvailability();
}

function hideAllViews() {
  const views = [
    "authView",
    "studentDashboard",
    "tutorDashboard",
    "searchTutorsView",
    "bookingManagementView",
    "subjectManagementView",
    "availabilityManagementView",
  ];
  views.forEach(
    (view) => (document.getElementById(view).style.display = "none")
  );
}

function backToDashboard() {
  showDashboard();
}

// Auth form switching
function showLoginForm(e) {
  e.preventDefault();
  document.getElementById("loginForm").style.display = "block";
  document.getElementById("signupForm").style.display = "none";
  document.getElementById("loginTab").classList.add("active");
  document.getElementById("signupTab").classList.remove("active");
}

function showSignupForm(e) {
  e.preventDefault();
  document.getElementById("loginForm").style.display = "none";
  document.getElementById("signupForm").style.display = "block";
  document.getElementById("signupTab").classList.add("active");
  document.getElementById("loginTab").classList.remove("active");
}

function toggleTutorFields() {
  const userType = document.getElementById("signupUserType").value;
  document.getElementById("tutorFields").style.display =
    userType === "TUTOR" ? "block" : "none";
}

// Authentication handlers
async function handleLogin(e) {
  e.preventDefault();
  const loginData = {
    email: document.getElementById("loginEmail").value,
    password: document.getElementById("loginPassword").value,
  };

  try {
    const response = await fetch(`${API_BASE_URL}/auth/login`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(loginData),
    });

    if (response.ok) {
      const userData = await response.json();
      state.currentUser = userData;
      localStorage.setItem("currentUser", JSON.stringify(userData));
      showDashboard();
    } else {
      const error = await response.text();
      document.getElementById("loginError").textContent = error;
      document.getElementById("loginError").style.display = "block";
    }
  } catch (error) {
    console.error("Login error:", error);
    document.getElementById("loginError").textContent = "Connection error";
    document.getElementById("loginError").style.display = "block";
  }
}

async function handleSignup(e) {
  e.preventDefault();
  const signupData = {
    userType: document.getElementById("signupUserType").value,
    name: document.getElementById("signupName").value,
    email: document.getElementById("signupEmail").value,
    password: document.getElementById("signupPassword").value,
    timeZoneId: document.getElementById("signupTimeZone").value,
    hourlyRate:
      parseFloat(document.getElementById("signupHourlyRate").value) || 0,
    description: document.getElementById("signupDescription").value || "",
  };

  try {
    const response = await fetch(`${API_BASE_URL}/auth/signup`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(signupData),
    });

    if (response.ok) {
      const userData = await response.json();
      state.currentUser = userData;
      localStorage.setItem("currentUser", JSON.stringify(userData));
      showDashboard();
    } else {
      const error = await response.text();
      document.getElementById("signupError").textContent = error;
      document.getElementById("signupError").style.display = "block";
    }
  } catch (error) {
    console.error("Signup error:", error);
    document.getElementById("signupError").textContent = "Connection error";
    document.getElementById("signupError").style.display = "block";
  }
}

function handleLogout() {
  state.currentUser = null;
  localStorage.removeItem("currentUser");
  showAuthView();
}

// Dashboard loading functions
async function loadStudentDashboard() {
  try {
    const response = await fetch(
      `${API_BASE_URL}/dashboard/student/${state.currentUser.id}`
    );
    if (response.ok) {
      const dashboard = await response.json();

      // Update profile
      document.getElementById("studentName").textContent =
        dashboard.profile.name;
      document.getElementById("studentBalance").textContent =
        dashboard.profile.balance.toFixed(2);

      // Update stats
      document.getElementById("totalSessions").textContent =
        dashboard.stats.totalSessions;
      document.getElementById("completedSessions").textContent =
        dashboard.stats.completedSessions;
      document.getElementById("upcomingSessions").textContent =
        dashboard.stats.upcomingSessions;

      // Update upcoming bookings
      renderUpcomingBookings(
        dashboard.upcomingBookings,
        "upcomingBookingsList"
      );
    }
  } catch (error) {
    console.error("Error loading student dashboard:", error);
  }
}

async function loadTutorDashboard() {
  try {
    const response = await fetch(
      `${API_BASE_URL}/dashboard/tutor/${state.currentUser.id}`
    );
    if (response.ok) {
      const dashboard = await response.json();

      // Update profile
      document.getElementById("tutorName").textContent = dashboard.profile.name;
      document.getElementById("tutorRate").textContent =
        dashboard.profile.hourlyRate.toFixed(2);

      // Update stats
      document.getElementById("tutorTotalSessions").textContent =
        dashboard.stats.totalSessions;
      document.getElementById("tutorCompletedSessions").textContent =
        dashboard.stats.completedSessions;
      document.getElementById("tutorRating").textContent =
        dashboard.stats.averageRating.toFixed(1);
      document.getElementById("tutorEarnings").textContent =
        dashboard.stats.totalEarnings.toFixed(2);
      document.getElementById("thisMonthEarnings").textContent =
        dashboard.stats.thisMonthEarnings.toFixed(2);
      document.getElementById("totalReviews").textContent =
        dashboard.stats.totalReviews;

      // Update today's schedule
      renderUpcomingBookings(dashboard.todaysSchedule, "todaysScheduleList");
    }
  } catch (error) {
    console.error("Error loading tutor dashboard:", error);
  }
}

// Search functionality
async function loadSubjectsForSearch() {
  try {
    const response = await fetch(`${API_BASE_URL}/subjects`);
    if (response.ok) {
      const data = await response.json();
      const select = document.getElementById("searchSubject");
      select.innerHTML = '<option value="">All Subjects</option>';

      data.subjects.forEach((category) => {
        category.subjects.forEach((subject) => {
          const option = document.createElement("option");
          option.value = subject.id;
          option.textContent = `${subject.name} (${category.category})`;
          select.appendChild(option);
        });
      });
    }
  } catch (error) {
    console.error("Error loading subjects:", error);
  }
}

async function handleSearch(e) {
  e.preventDefault();

  const searchData = {
    subjectId: document.getElementById("searchSubject").value || null,
    minPrice: parseFloat(document.getElementById("minPrice").value) || 0,
    maxPrice: parseFloat(document.getElementById("maxPrice").value) || 0,
    minRating: parseFloat(document.getElementById("minRating").value) || 0,
    sortBy: document.getElementById("sortBy").value,
    page: 0,
    pageSize: 20,
  };

  try {
    const response = await fetch(`${API_BASE_URL}/search/tutors`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(searchData),
    });

    if (response.ok) {
      const results = await response.json();
      renderSearchResults(results.results);
    }
  } catch (error) {
    console.error("Search error:", error);
  }
}

function renderSearchResults(tutors) {
  const container = document.getElementById("searchResults");
  container.innerHTML = "";

  if (tutors.length === 0) {
    container.innerHTML =
      '<p class="text-center">No tutors found matching your criteria.</p>';
    return;
  }

  tutors.forEach((tutor) => {
    const card = document.createElement("div");
    card.className = "card mb-3";
    card.innerHTML = `
            <div class="card-body">
                <div class="row">
                    <div class="col-md-8">
                        <h5 class="card-title">${tutor.name}</h5>
                        <p class="card-text">${tutor.shortDescription}</p>
                        <p>Rate: ${
                          tutor.hourlyRate
                        }/hr | Rating: ${tutor.rating.toFixed(1)}⭐ (${
      tutor.reviewCount
    } reviews)</p>
                        <p>Subjects: ${tutor.subjects
                          .map((s) => s.name)
                          .join(", ")}</p>
                    </div>
                    <div class="col-md-4 text-end">
                        <button class="btn btn-primary" onclick="bookTutor('${
                          tutor.id
                        }', '${tutor.name}', ${
      tutor.hourlyRate
    })">Book Session</button>
                        <button class="btn btn-secondary" onclick="viewTutorDetails('${
                          tutor.id
                        }')">View Profile</button>
                    </div>
                </div>
            </div>
        `;
    container.appendChild(card);
  });
}

// Booking management
async function showBookingTab(tab) {
  const userId = state.currentUser.id;
  const endpoint =
    state.currentView === "studentBookings"
      ? `${API_BASE_URL}/bookings/student/${userId}`
      : `${API_BASE_URL}/bookings/tutor/${userId}`;

  try {
    const response = await fetch(endpoint);
    if (response.ok) {
      const data = await response.json();
      const bookings =
        tab === "upcoming"
          ? data.upcomingBookings
          : tab === "past"
          ? data.pastBookings
          : data.cancelledBookings;

      renderBookings(bookings, tab);

      // Update tab active states
      document
        .querySelectorAll("#bookingManagementView .nav-link")
        .forEach((link) => {
          link.classList.remove("active");
        });
      document.getElementById(`${tab}Tab`).classList.add("active");
    }
  } catch (error) {
    console.error("Error loading bookings:", error);
  }
}

function renderBookings(bookings, tab) {
  const container = document.getElementById("bookingsList");
  container.innerHTML = "";

  if (bookings.length === 0) {
    container.innerHTML = `<p class="text-center">No ${tab} bookings.</p>`;
    return;
  }

  bookings.forEach((booking) => {
    const card = document.createElement("div");
    card.className = "card mb-3";
    const dateTime = new Date(booking.dateTime).toLocaleString();

    card.innerHTML = `
            <div class="card-body">
                <div class="row">
                    <div class="col-md-8">
                        <h5 class="card-title">${booking.subject.name}</h5>
                        <p>With: ${
                          state.currentView === "studentBookings"
                            ? booking.tutor.name
                            : booking.student.name
                        }</p>
                        <p>Date: ${dateTime} | Duration: ${
      booking.durationHours
    } hour(s)</p>
                        <p>Total Cost: ${booking.totalCost} | Status: ${
      booking.status
    }</p>
                    </div>
                    <div class="col-md-4 text-end">
                        ${getBookingActions(booking, tab)}
                    </div>
                </div>
            </div>
        `;
    container.appendChild(card);
  });
}

function getBookingActions(booking, tab) {
  if (tab === "upcoming" && booking.status === "PENDING") {
    return `
            <button class="btn btn-success btn-sm confirm-booking-btn" data-booking-id="${booking.id}">Confirm & Pay</button>
            <button class="btn btn-danger btn-sm cancel-booking-btn" data-booking-id="${booking.id}">Cancel</button>
        `;
  } else if (tab === "upcoming" && booking.status === "CONFIRMED") {
    return `<button class="btn btn-danger btn-sm cancel-booking-btn" data-booking-id="${booking.id}">Cancel</button>`;
  } else if (
    tab === "past" &&
    booking.status === "COMPLETED" &&
    state.currentView === "studentBookings"
  ) {
    return `<button class="btn btn-primary btn-sm review-tutor-btn" data-tutor-id="${booking.tutor.id}">Leave Review</button>`;
  }
  return "";
}

function renderUpcomingBookings(bookings, containerId) {
  const container = document.getElementById(containerId);
  container.innerHTML = "";

  if (bookings.length === 0) {
    container.innerHTML = '<p class="text-muted">No upcoming sessions.</p>';
    return;
  }

  bookings.forEach((booking) => {
    const dateTime = new Date(booking.dateTime).toLocaleString();
    const div = document.createElement("div");
    div.className = "border-bottom pb-2 mb-2";
    div.innerHTML = `
            <strong>${booking.subject.name}</strong><br>
            With: ${
              state.currentUser.userType === "Student"
                ? booking.tutor.name
                : booking.student.name
            }<br>
            ${dateTime}
        `;
    container.appendChild(div);
  });
}

// Subject management
async function loadTutorSubjects() {
  try {
    // Load tutor's current subjects
    const tutorResponse = await fetch(
      `${API_BASE_URL}/tutors/${state.currentUser.id}`
    );
    if (tutorResponse.ok) {
      const tutorData = await tutorResponse.json();
      renderMySubjects(tutorData.subjects);
    }

    // Load available subjects
    const availableResponse = await fetch(
      `${API_BASE_URL}/subjects/available/tutor/${state.currentUser.id}`
    );
    if (availableResponse.ok) {
      const availableSubjects = await availableResponse.json();
      renderAvailableSubjects(availableSubjects);
    }
  } catch (error) {
    console.error("Error loading subjects:", error);
  }
}

function renderMySubjects(subjects) {
  const container = document.getElementById("mySubjectsList");
  container.innerHTML = "";

  if (subjects.length === 0) {
    container.innerHTML = '<p class="text-muted">No subjects added yet.</p>';
    return;
  }

  subjects.forEach((subject) => {
    const div = document.createElement("div");
    div.className = "d-flex justify-content-between align-items-center mb-2";
    div.innerHTML = `
            <span>${subject.name} (${subject.category})</span>
            <button class="btn btn-sm btn-danger remove-subject-btn" data-subject-id="${subject.id}">Remove</button>
        `;
    container.appendChild(div);
  });
}

function renderAvailableSubjects(subjects) {
  const container = document.getElementById("availableSubjectsList");
  container.innerHTML = "";

  if (subjects.length === 0) {
    container.innerHTML =
      '<p class="text-muted">All subjects already added.</p>';
    return;
  }

  subjects.forEach((subject) => {
    const div = document.createElement("div");
    div.className = "d-flex justify-content-between align-items-center mb-2";
    div.innerHTML = `
            <span>${subject.name} (${subject.category})</span>
            <button class="btn btn-sm btn-success add-subject-btn" data-subject-id="${subject.id}">Add</button>
        `;
    container.appendChild(div);
  });
}

async function addSubject(subjectId) {
  try {
    const response = await fetch(
      `${API_BASE_URL}/tutors/${state.currentUser.id}/subjects`,
      {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ subjectId }),
      }
    );

    if (response.ok) {
      loadTutorSubjects();
    }
  } catch (error) {
    console.error("Error adding subject:", error);
  }
}

async function removeSubject(subjectId) {
  try {
    const response = await fetch(
      `${API_BASE_URL}/tutors/${state.currentUser.id}/subjects/${subjectId}`,
      {
        method: "DELETE",
      }
    );

    if (response.ok) {
      loadTutorSubjects();
    }
  } catch (error) {
    console.error("Error removing subject:", error);
  }
}

// Availability management
async function loadTutorAvailability() {
  try {
    const response = await fetch(
      `${API_BASE_URL}/tutors/${state.currentUser.id}/availability`
    );
    if (response.ok) {
      const availability = await response.json();
      renderAvailability(availability.regularSchedule);
    }
  } catch (error) {
    console.error("Error loading availability:", error);
  }
}

function renderAvailability(slots) {
  const container = document.getElementById("availabilityList");
  container.innerHTML = "";

  if (slots.length === 0) {
    container.innerHTML = '<p class="text-muted">No availability set.</p>';
    return;
  }

  const dayOrder = [
    "MONDAY",
    "TUESDAY",
    "WEDNESDAY",
    "THURSDAY",
    "FRIDAY",
    "SATURDAY",
    "SUNDAY",
  ];
  slots.sort(
    (a, b) => dayOrder.indexOf(a.dayOfWeek) - dayOrder.indexOf(b.dayOfWeek)
  );

  slots.forEach((slot) => {
    const div = document.createElement("div");
    div.className = "d-flex justify-content-between align-items-center mb-2";
    div.innerHTML = `
            <span>${slot.dayOfWeek}: ${slot.startTime} - ${slot.endTime}</span>
            <button class="btn btn-sm btn-danger remove-availability-btn" 
                    data-day-of-week="${slot.dayOfWeek}" 
                    data-start-time="${slot.startTime}" 
                    data-end-time="${slot.endTime}">Remove</button>
        `;
    container.appendChild(div);
  });
}

async function handleAddAvailability(e) {
  e.preventDefault();

  const data = {
    action: "ADD",
    dayOfWeek: document.getElementById("dayOfWeek").value,
    startTime: document.getElementById("startTime").value,
    endTime: document.getElementById("endTime").value,
  };

  try {
    const response = await fetch(
      `${API_BASE_URL}/tutors/${state.currentUser.id}/availability`,
      {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data),
      }
    );

    if (response.ok) {
      document.getElementById("availabilityForm").reset();
      loadTutorAvailability();
    }
  } catch (error) {
    console.error("Error adding availability:", error);
  }
}

async function removeAvailability(dayOfWeek, startTime, endTime) {
  const data = {
    action: "REMOVE",
    dayOfWeek,
    startTime,
    endTime,
  };

  try {
    const response = await fetch(
      `${API_BASE_URL}/tutors/${state.currentUser.id}/availability`,
      {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data),
      }
    );

    if (response.ok) {
      loadTutorAvailability();
    }
  } catch (error) {
    console.error("Error removing availability:", error);
  }
}

// Profile management
function showEditProfileModal() {
  if (state.currentUser.userType === "Student") {
    document.getElementById("tutorEditFields").style.display = "none";
  } else {
    document.getElementById("tutorEditFields").style.display = "block";
  }
  profileEditModal.show();
}

async function handleUpdateProfile() {
  const updateData = {
    name: document.getElementById("editName").value || null,
    email: document.getElementById("editEmail").value || null,
    currentPassword:
      document.getElementById("editCurrentPassword").value || null,
    password: document.getElementById("editNewPassword").value || null,
    timeZoneId: document.getElementById("editTimeZone").value || null,
  };

  if (state.currentUser.userType === "Tutor") {
    updateData.hourlyRate =
      parseFloat(document.getElementById("editHourlyRate").value) || null;
    updateData.description =
      document.getElementById("editDescription").value || null;
  }

  const endpoint =
    state.currentUser.userType === "Student"
      ? `${API_BASE_URL}/students/${state.currentUser.id}`
      : `${API_BASE_URL}/tutors/${state.currentUser.id}`;

  try {
    const response = await fetch(endpoint, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(updateData),
    });

    if (response.ok) {
      const updatedProfile = await response.json();
      // Update current user data
      Object.assign(state.currentUser, updatedProfile);
      localStorage.setItem("currentUser", JSON.stringify(state.currentUser));
      profileEditModal.hide();
      showDashboard();
    }
  } catch (error) {
    console.error("Error updating profile:", error);
  }
}

// Funds management
function showAddFundsModal() {
  addFundsModal.show();
}

async function handleAddFunds() {
  const amount = parseFloat(document.getElementById("fundAmount").value);

  try {
    const response = await fetch(
      `${API_BASE_URL}/students/${state.currentUser.id}/add-funds`,
      {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ amount }),
      }
    );

    if (response.ok) {
      const result = await response.json();
      state.currentUser.balance = result.value;
      localStorage.setItem("currentUser", JSON.stringify(state.currentUser));
      addFundsModal.hide();
      document.getElementById("addFundsForm").reset();
      loadStudentDashboard();
    }
  } catch (error) {
    console.error("Error adding funds:", error);
  }
}

// Booking functions
async function bookTutor(tutorId, tutorName, hourlyRate) {
  document.getElementById("bookingTutorName").textContent = tutorName;
  document.getElementById("bookingTutorId").value = tutorId;

  // Load tutor's subjects
  try {
    const response = await fetch(`${API_BASE_URL}/tutors/${tutorId}`);
    if (response.ok) {
      const tutorData = await response.json();
      const select = document.getElementById("bookingSubject");
      select.innerHTML = '<option value="">Select subject...</option>';

      tutorData.subjects.forEach((subject) => {
        const option = document.createElement("option");
        option.value = subject.id;
        option.textContent = subject.name;
        select.appendChild(option);
      });

      // Store hourly rate for cost calculation
      select.dataset.hourlyRate = hourlyRate;
      updateBookingCost();
    }
  } catch (error) {
    console.error("Error loading tutor subjects:", error);
  }

  createBookingModal.show();
}

function updateBookingCost() {
  const duration =
    parseInt(document.getElementById("bookingDuration").value) || 1;
  const hourlyRate =
    parseFloat(document.getElementById("bookingSubject").dataset.hourlyRate) ||
    0;
  const totalCost = duration * hourlyRate;
  document.getElementById("bookingTotalCost").textContent =
    totalCost.toFixed(2);
}

async function handleCreateBooking() {
  const bookingData = {
    studentId: state.currentUser.id,
    tutorId: document.getElementById("bookingTutorId").value,
    subjectId: document.getElementById("bookingSubject").value,
    dateTime: `${document.getElementById("bookingDate").value}T${
      document.getElementById("bookingTime").value
    }:00`,
    durationHours: parseInt(document.getElementById("bookingDuration").value),
  };

  try {
    const response = await fetch(`${API_BASE_URL}/bookings`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(bookingData),
    });

    if (response.ok) {
      createBookingModal.hide();
      document.getElementById("createBookingForm").reset();
      alert(
        "Booking created successfully! Please confirm and pay to secure your session."
      );
      showStudentBookings();
    } else {
      const error = await response.text();
      alert(`Error creating booking: ${error}`);
    }
  } catch (error) {
    console.error("Error creating booking:", error);
  }
}

async function confirmBooking(bookingId) {
  try {
    const response = await fetch(
      `${API_BASE_URL}/bookings/${bookingId}/confirm`,
      {
        method: "POST",
      }
    );

    if (response.ok) {
      alert("Booking confirmed and payment processed!");
      showBookingTab("upcoming");
      loadStudentDashboard();
    } else {
      const error = await response.text();
      alert(`Error confirming booking: ${error}`);
    }
  } catch (error) {
    console.error("Error confirming booking:", error);
  }
}

async function cancelBooking(bookingId) {
  if (!confirm("Are you sure you want to cancel this booking?")) {
    return;
  }

  try {
    const response = await fetch(
      `${API_BASE_URL}/bookings/${bookingId}/cancel`,
      {
        method: "POST",
      }
    );

    if (response.ok) {
      alert("Booking cancelled successfully!");
      showBookingTab("upcoming");
    }
  } catch (error) {
    console.error("Error cancelling booking:", error);
  }
}

// Review functions
function showReviewModal(tutorId) {
  document.getElementById("reviewTutorId").value = tutorId;
  reviewModal.show();
}

async function handleSubmitReview() {
  const reviewData = {
    studentId: state.currentUser.id,
    tutorId: document.getElementById("reviewTutorId").value,
    rating: parseInt(document.getElementById("reviewRating").value),
    comment: document.getElementById("reviewComment").value,
  };

  try {
    const response = await fetch(`${API_BASE_URL}/reviews`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(reviewData),
    });

    if (response.ok) {
      reviewModal.hide();
      document.getElementById("reviewForm").reset();
      alert("Review submitted successfully!");
    }
  } catch (error) {
    console.error("Error submitting review:", error);
  }
}

// Tutor details
async function viewTutorDetails(tutorId) {
  try {
    const response = await fetch(`${API_BASE_URL}/tutors/${tutorId}`);
    if (response.ok) {
      const tutor = await response.json();

      const content = document.getElementById("tutorDetailsContent");
      content.innerHTML = `
                <h4>${tutor.name}</h4>
                <p><strong>Rate:</strong> ${tutor.hourlyRate}/hr</p>
                <p><strong>Rating:</strong> ${tutor.rating.toFixed(1)}⭐ (${
        tutor.totalReviews
      } reviews)</p>
                <p><strong>Description:</strong> ${tutor.description}</p>
                <p><strong>Subjects:</strong> ${tutor.subjects
                  .map((s) => s.name)
                  .join(", ")}</p>
                <p><strong>Completed Sessions:</strong> ${
                  tutor.completedSessions
                }</p>
                <p><strong>Member Since:</strong> ${new Date(
                  tutor.joinedDate
                ).toLocaleDateString()}</p>
                <hr>
                <h5>Reviews</h5>
                <div id="tutorReviews"></div>
            `;

      // Load and display reviews
      loadTutorReviews(tutorId);
      tutorDetailsModal.show();
    }
  } catch (error) {
    console.error("Error loading tutor details:", error);
  }
}

async function loadTutorReviews(tutorId) {
  try {
    const response = await fetch(`${API_BASE_URL}/reviews/tutor/${tutorId}`);
    if (response.ok) {
      const reviews = await response.json();
      const container = document.getElementById("tutorReviews");

      if (reviews.length === 0) {
        container.innerHTML = '<p class="text-muted">No reviews yet.</p>';
        return;
      }

      container.innerHTML = reviews
        .map(
          (review) => `
                <div class="border-bottom pb-2 mb-2">
                    <strong>${review.studentInfo.name}</strong> - ${
            review.rating
          }⭐
                    <p class="mb-0">${review.comment}</p>
                    <small class="text-muted">${new Date(
                      review.createdAt
                    ).toLocaleDateString()}</small>
                </div>
            `
        )
        .join("");
    }
  } catch (error) {
    console.error("Error loading reviews:", error);
  }
}
