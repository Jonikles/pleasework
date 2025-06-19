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

// Loading state management
const loadingState = {
  isLoading: false,
  loadingMessage: ''
};

// Notification polling interval
let notificationInterval;

// Initialize the application
document.addEventListener("DOMContentLoaded", () => {
  initializeModals();
  initializeEventListeners();
  initializeTimezoneSelects();
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

  // Logo click to go to dashboard
  document.querySelector(".navbar-brand").addEventListener("click", () => {
    if (state.currentUser) {
      showDashboard();
    }
  });

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

  // Notification listeners
  document.getElementById('markAllReadBtn').addEventListener('click', markAllNotificationsRead);

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
    } else if (e.target.classList.contains("complete-booking-btn")) {
      const bookingId = e.target.dataset.bookingId;
      completeBooking(bookingId);
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

  // Form validation listeners
  addFormValidationListeners();

  // Keyboard shortcuts
  addKeyboardShortcuts();

  // Auto-save listeners
  addAutoSaveListeners();
}

// Initialize timezone selects
function initializeTimezoneSelects() {
  // This function is called but not implemented in the original code
  // You can leave it empty if timezones are hardcoded in HTML
}

// Loading overlay functions
function showLoading(message = 'Loading...') {
  loadingState.isLoading = true;
  loadingState.loadingMessage = message;
  
  const overlay = document.getElementById('loadingOverlay');
  if (overlay) {
    overlay.querySelector('.loading-message').textContent = message;
    overlay.style.display = 'flex';
  }
}

function hideLoading() {
  loadingState.isLoading = false;
  const overlay = document.getElementById('loadingOverlay');
  if (overlay) {
    overlay.style.display = 'none';
  }
}

// Notification functions
function startNotificationPolling() {
  // Initial load
  loadNotifications();
  
  // Poll every 30 seconds
  notificationInterval = setInterval(loadNotifications, 30000);
}

function stopNotificationPolling() {
  if (notificationInterval) {
    clearInterval(notificationInterval);
    notificationInterval = null;
  }
}

async function loadNotifications() {
  if (!state.currentUser) return;
  
  try {
    const response = await fetch(`${API_BASE_URL}/notifications/user/${state.currentUser.id}/unread`);
    if (response.ok) {
      const data = await response.json();
      updateNotificationUI(data);
    }
  } catch (error) {
    console.error("Error loading notifications:", error);
  }
}

function updateNotificationUI(data) {
  const countBadge = document.getElementById('notificationCount');
  const notificationList = document.getElementById('notificationList');
  
  // Update count
  if (data.unreadCount > 0) {
    countBadge.textContent = data.unreadCount > 9 ? '9+' : data.unreadCount;
    countBadge.style.display = 'inline-block';
  } else {
    countBadge.style.display = 'none';
  }
  
  // Update notification list
  notificationList.innerHTML = '';
  
  if (data.notifications.length === 0) {
    notificationList.innerHTML = '<div class="dropdown-item text-muted">No new notifications</div>';
    return;
  }
  
  data.notifications.forEach(notification => {
    const notifElement = document.createElement('div');
    notifElement.className = `dropdown-item notification-item ${!notification.read ? 'unread' : ''}`;
    notifElement.innerHTML = `
      <div class="d-flex justify-content-between align-items-start">
        <div>
          <h6 class="mb-1">${notification.title}</h6>
          <p class="mb-0 small">${notification.message}</p>
          <small class="text-muted">${formatTimeAgo(notification.createdAt)}</small>
        </div>
        ${notification.actionUrl ? `<i class="bi bi-chevron-right"></i>` : ''}
      </div>
    `;
    
    notifElement.addEventListener('click', () => handleNotificationClick(notification));
    notificationList.appendChild(notifElement);
  });
}

async function handleNotificationClick(notification) {
  // Mark as read
  try {
    await fetch(`${API_BASE_URL}/notifications/${notification.id}/read`, {
      method: 'PUT'
    });
    
    // Reload notifications
    loadNotifications();
    
    // Navigate if action URL exists
    if (notification.actionUrl) {
      // Handle navigation based on URL
      if (notification.actionUrl.includes('/bookings/')) {
        showBookingManagement();
      }
    }
  } catch (error) {
    console.error("Error marking notification as read:", error);
  }
}

async function markAllNotificationsRead() {
  try {
    await fetch(`${API_BASE_URL}/notifications/user/${state.currentUser.id}/read-all`, {
      method: 'PUT'
    });
    loadNotifications();
  } catch (error) {
    console.error("Error marking all notifications as read:", error);
  }
}

// Validation utilities
const validators = {
  email: (email) => {
    const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return re.test(email);
  },
  
  password: (password) => {
    return password.length >= 8;
  },
  
  hourlyRate: (rate) => {
    return rate > 0 && rate <= 1000;
  },
  
  description: (desc) => {
    return desc && desc.trim().length >= 50 && desc.length <= 1000;
  }
};

function addFormValidationListeners() {
  // Add real-time validation
  document.getElementById('signupEmail').addEventListener('blur', function() {
    const email = this.value;
    const isValid = validators.email(email);
    this.classList.toggle('is-invalid', !isValid && email.length > 0);
  });

  document.getElementById('signupPassword').addEventListener('input', function() {
    const password = this.value;
    const isValid = validators.password(password);
    this.classList.toggle('is-invalid', !isValid && password.length > 0);
    
    // Show password strength
    const strengthIndicator = document.getElementById('passwordStrength');
    if (strengthIndicator) {
      const strength = calculatePasswordStrength(password);
      strengthIndicator.textContent = strength.message;
      strengthIndicator.className = `form-text ${strength.class}`;
    }
  });
}

function calculatePasswordStrength(password) {
  if (password.length < 8) return { message: 'Too short', class: 'text-danger' };
  
  let strength = 0;
  if (password.match(/[a-z]+/)) strength++;
  if (password.match(/[A-Z]+/)) strength++;
  if (password.match(/[0-9]+/)) strength++;
  if (password.match(/[$@#&!]+/)) strength++;
  
  if (strength < 2) return { message: 'Weak password', class: 'text-warning' };
  if (strength < 3) return { message: 'Good password', class: 'text-info' };
  return { message: 'Strong password', class: 'text-success' };
}

// Empty state component
function createEmptyState(type) {
  const emptyStates = {
    bookings: {
      icon: 'calendar-x',
      title: 'No bookings yet',
      message: 'Start by finding a tutor and booking your first session!',
      action: state.currentUser?.userType === 'Student' ? 
        '<button class="btn btn-primary" onclick="showSearchTutors()">Find a Tutor</button>' : ''
    },
    subjects: {
      icon: 'book',
      title: 'No subjects added',
      message: 'Add subjects you want to teach to start receiving bookings.',
      action: ''
    },
    notifications: {
      icon: 'bell-slash',
      title: 'No notifications',
      message: 'You\'re all caught up!',
      action: ''
    },
    search: {
      icon: 'search',
      title: 'No tutors found',
      message: 'Try adjusting your search filters or browse all tutors.',
      action: '<button class="btn btn-secondary" onclick="clearSearchFilters()">Clear Filters</button>'
    }
  };
  
  const stateConfig = emptyStates[type];
  return `
    <div class="empty-state">
      <i class="bi bi-${stateConfig.icon}"></i>
      <h5>${stateConfig.title}</h5>
      <p>${stateConfig.message}</p>
      ${stateConfig.action}
    </div>
  `;
}

// Toast notification utility
function showToast(title, message, type = 'info') {
  const toastEl = document.getElementById('toast');
  const toastTitle = document.getElementById('toastTitle');
  const toastMessage = document.getElementById('toastMessage');
  
  // Set content
  toastTitle.textContent = title;
  toastMessage.textContent = message;
  
  // Set color based on type
  toastEl.className = `toast toast-${type}`;
  
  // Show toast
  const toast = new bootstrap.Toast(toastEl);
  toast.show();
}

// Time formatting functions
function formatTimeAgo(dateString) {
  const date = new Date(dateString);
  const now = new Date();
  const seconds = Math.floor((now - date) / 1000);
  
  if (seconds < 60) return 'just now';
  const minutes = Math.floor(seconds / 60);
  if (minutes < 60) return `${minutes} minute${minutes > 1 ? 's' : ''} ago`;
  const hours = Math.floor(minutes / 60);
  if (hours < 24) return `${hours} hour${hours > 1 ? 's' : ''} ago`;
  const days = Math.floor(hours / 24);
  if (days < 7) return `${days} day${days > 1 ? 's' : ''} ago`;
  const weeks = Math.floor(days / 7);
  return `${weeks} week${weeks > 1 ? 's' : ''} ago`;
}

// Rating display function
function displayRating(rating) {
  if (rating === 0) return '<span class="not-rated">Not rated yet</span>';

  const fullStars = Math.floor(rating);
  const hasHalfStar = rating % 1 >= 0.5;
  let stars = "";

  for (let i = 0; i < fullStars; i++) {
    stars += '<i class="bi bi-star-fill"></i>';
  }
  if (hasHalfStar) {
    stars += '<i class="bi bi-star-half"></i>';
  }

  // Add empty stars to complete 5
  const emptyStars = 5 - fullStars - (hasHalfStar ? 1 : 0);
  for (let i = 0; i < emptyStars; i++) {
    stars += '<i class="bi bi-star"></i>';
  }

  return `${stars} <span class="rating-number">${rating.toFixed(1)}</span>`;
}

// Session countdown timer
function startSessionCountdown() {
  const updateCountdowns = () => {
    document.querySelectorAll('.session-countdown').forEach(el => {
      const sessionTime = new Date(el.dataset.sessionTime);
      const now = new Date();
      const diff = sessionTime - now;
      
      if (diff > 0) {
        const hours = Math.floor(diff / (1000 * 60 * 60));
        const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));
        
        if (hours < 24) {
          el.textContent = `Starts in ${hours}h ${minutes}m`;
          el.classList.add('text-warning');
        } else {
          const days = Math.floor(hours / 24);
          el.textContent = `In ${days} day${days > 1 ? 's' : ''}`;
        }
      } else {
        el.textContent = 'Session started';
        el.classList.add('text-danger');
      }
    });
  };
  
  updateCountdowns();
  setInterval(updateCountdowns, 60000); // Update every minute
}

// Keyboard shortcuts
function addKeyboardShortcuts() {
  document.addEventListener('keydown', (e) => {
    if (!state.currentUser) return;
    
    // Ctrl/Cmd + K - Quick search
    if ((e.ctrlKey || e.metaKey) && e.key === 'k') {
      e.preventDefault();
      if (state.currentUser.userType === 'Student') {
        showSearchTutors();
      }
    }
    
    // Escape - Close modals
    if (e.key === 'Escape') {
      const modals = document.querySelectorAll('.modal.show');
      modals.forEach(modal => {
        const modalInstance = bootstrap.Modal.getInstance(modal);
        if (modalInstance) modalInstance.hide();
      });
    }
  });
}

// Auto-save functionality
let autoSaveTimeout;

function addAutoSaveListeners() {
  document.getElementById('editDescription')?.addEventListener('input', function() {
    clearTimeout(autoSaveTimeout);
    const value = this.value;
    
    // Show saving indicator
    const indicator = document.getElementById('autoSaveIndicator');
    if (indicator) {
      indicator.textContent = 'Typing...';
      indicator.className = 'text-muted small';
    }
    
    autoSaveTimeout = setTimeout(() => {
      // Save to localStorage
      localStorage.setItem('draft_description', value);
      if (indicator) {
        indicator.textContent = 'Draft saved';
        indicator.className = 'text-success small';
      }
    }, 1000);
  });
}

// Confirmation dialog
function showConfirmDialog(title, message, confirmText = 'Confirm', type = 'warning') {
  return new Promise((resolve) => {
    const modal = new bootstrap.Modal(document.getElementById('confirmModal'));
    
    document.getElementById('confirmTitle').textContent = title;
    document.getElementById('confirmMessage').textContent = message;
    const confirmBtn = document.getElementById('confirmActionBtn');
    confirmBtn.textContent = confirmText;
    confirmBtn.className = `btn btn-${type}`;
    
    confirmBtn.onclick = () => {
      modal.hide();
      resolve(true);
    };
    
    document.getElementById('confirmCancelBtn').onclick = () => {
      modal.hide();
      resolve(false);
    };
    
    modal.show();
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

  // Start notification polling
  startNotificationPolling();

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
  showLoading('Logging in...');
  
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
  } finally {
    hideLoading();
  }
}

async function handleSignup(e) {
  e.preventDefault();
  showLoading('Creating account...');
  
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
  } finally {
    hideLoading();
  }
}

function handleLogout() {
  state.currentUser = null;
  localStorage.removeItem("currentUser");
  stopNotificationPolling();
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
      
      // Update profile picture
      if (dashboard.profile.profilePictureUrl) {
        document.getElementById("studentProfilePic").src = dashboard.profile.profilePictureUrl;
      }

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
      
      // Update profile picture
      if (dashboard.profile.profilePictureUrl) {
        document.getElementById("tutorProfilePic").src = dashboard.profile.profilePictureUrl;
      }

      // Update stats
      document.getElementById("tutorTotalSessions").textContent =
        dashboard.stats.totalSessions;
      document.getElementById("tutorCompletedSessions").textContent =
        dashboard.stats.completedSessions;
      document.getElementById("tutorRating").innerHTML = displayRating(
        dashboard.stats.averageRating
      );
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
  showLoading("Searching...");

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
  } finally {
    hideLoading();
  }
}

function renderSearchResults(tutors) {
  const container = document.getElementById("searchResults");
  container.innerHTML = "";

  if (tutors.length === 0) {
    container.innerHTML = createEmptyState("search");
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
                        <p>Rate: $${
                          tutor.hourlyRate
                            }/hr | Rating: ${tutor.rating.toFixed(1)} (${
      tutor.reviewCount
    } reviews)</p>
                        <p>Subjects: ${tutor.subjects
                          .map((s) => s.name)
                          .join(", ")}</p>
                    </div>
                    <div class="col-md-4 text-end">
                        <button class="btn btn-primary book-tutor-btn" 
                                data-tutor-id="${tutor.id}" 
                                data-tutor-name="${tutor.name}" 
                                data-hourly-rate="${
                                  tutor.hourlyRate
                                }">Book Session</button>
                        <button class="btn btn-secondary view-tutor-btn" 
                                data-tutor-id="${
                                  tutor.id
                                }">View Profile</button>
                    </div>
                </div>
            </div>
        `;
    container.appendChild(card);
  });
}

// Clear search filters
function clearSearchFilters() {
  document.getElementById("searchForm").reset();
  handleSearch(new Event("submit"));
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
    container.innerHTML = createEmptyState("bookings");
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
    // Add complete button for tutors
    const completeButton =
      state.currentView === "tutorBookings" &&
      new Date(booking.dateTime) < new Date()
        ? `<button class="btn btn-primary btn-sm complete-booking-btn" data-booking-id="${booking.id}">Mark Complete</button>`
        : "";
    return `
      ${completeButton}
      <button class="btn btn-danger btn-sm cancel-booking-btn" data-booking-id="${booking.id}">Cancel</button>
    `;
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
    const dateTime = new Date(booking.dateTime);
    const div = document.createElement("div");
    div.className = "session-item";
    div.innerHTML = `
      <div class="d-flex justify-content-between align-items-start">
        <div>
          <strong>${booking.subject.name}</strong><br>
          With: ${
            state.currentUser.userType === "Student"
              ? booking.tutor.name
              : booking.student.name
          }<br>
          ${dateTime.toLocaleString()}
        </div>
        <small class="session-countdown" data-session-time="${
          booking.dateTime
        }"></small>
      </div>
    `;
    container.appendChild(div);
  });

  startSessionCountdown();
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
    container.innerHTML = createEmptyState("subjects");
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
      showToast("Success", "Subject added successfully", "success");
    }
  } catch (error) {
    console.error("Error adding subject:", error);
    showToast("Error", "Failed to add subject", "danger");
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
      showToast("Success", "Subject removed successfully", "success");
    }
  } catch (error) {
    console.error("Error removing subject:", error);
    showToast("Error", "Failed to remove subject", "danger");
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
      showToast("Success", "Availability added successfully", "success");
    }
  } catch (error) {
    console.error("Error adding availability:", error);
    showToast("Error", "Failed to add availability", "danger");
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
      showToast("Success", "Availability removed successfully", "success");
    }
  } catch (error) {
    console.error("Error removing availability:", error);
    showToast("Error", "Failed to remove availability", "danger");
  }
}

// Profile management
function showEditProfileModal() {
  if (state.currentUser.userType === "Student") {
    document.getElementById("tutorEditFields").style.display = "none";
  } else {
    document.getElementById("tutorEditFields").style.display = "block";

    // Restore draft for tutors
    const savedDescription = localStorage.getItem("draft_description");
    if (savedDescription) {
      document.getElementById("editDescription").value = savedDescription;
    }
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

      // Handle profile picture upload if file selected
      const fileInput = document.getElementById("profilePictureUpload");
      if (fileInput.files.length > 0) {
        await uploadProfilePicture(fileInput.files[0]);
      }

      // Update current user data
      Object.assign(state.currentUser, updatedProfile);
      localStorage.setItem("currentUser", JSON.stringify(state.currentUser));
      profileEditModal.hide();
      showDashboard();
      showToast("Success", "Profile updated successfully", "success");
    }
  } catch (error) {
    console.error("Error updating profile:", error);
    showToast("Error", "Failed to update profile", "danger");
  }
}

// Profile picture upload
async function uploadProfilePicture(file) {
  const formData = new FormData();
  formData.append("file", file);
  formData.append("type", "profile");

  const endpoint =
    state.currentUser.userType === "Student"
      ? `${API_BASE_URL}/students/${state.currentUser.id}/profile-picture`
      : `${API_BASE_URL}/tutors/${state.currentUser.id}/profile-picture`;

  try {
    const response = await fetch(endpoint, {
      method: "POST",
      body: formData,
    });

    if (response.ok) {
      const result = await response.json();
      state.currentUser.profilePictureUrl = result.profilePictureUrl;
    }
  } catch (error) {
    console.error("Error uploading profile picture:", error);
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
      showToast(
        "Success",
        `$${amount.toFixed(2)} added to your balance`,
        "success"
      );
    }
  } catch (error) {
    console.error("Error adding funds:", error);
    showToast("Error", "Failed to add funds", "danger");
  }
}

// Booking functions
async function bookTutor(tutorId, tutorName, hourlyRate) {
  document.getElementById("bookingTutorName").textContent = tutorName;
  document.getElementById("bookingTutorId").value = tutorId;

  // Set minimum date to today
  const today = new Date().toISOString().split("T")[0];
  document.getElementById("bookingDate").setAttribute("min", today);

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
      showToast(
        "Success",
        "Booking created! Please confirm and pay to secure your session.",
        "success"
      );
      showStudentBookings();
    } else {
      const error = await response.text();
      const errorDiv = document.querySelector(
        "#createBookingModal .modal-error"
      );
      if (errorDiv) {
        errorDiv.textContent = `Error: ${error}`;
        errorDiv.style.display = "block";
      }
    }
  } catch (error) {
    console.error("Error creating booking:", error);
    showToast("Error", "Failed to create booking", "danger");
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
      showToast(
        "Success",
        "Booking confirmed and payment processed!",
        "success"
      );
      showBookingTab("upcoming");
      loadStudentDashboard();
    } else {
      const error = await response.text();
      showToast("Error", `Failed to confirm booking: ${error}`, "danger");
    }
  } catch (error) {
    console.error("Error confirming booking:", error);
    showToast("Error", "Connection error. Please try again.", "danger");
  }
}

async function cancelBooking(bookingId) {
  const confirmed = await showConfirmDialog(
    "Cancel Booking",
    "Are you sure you want to cancel this booking? If you've already paid, you will receive a full refund.",
    "Yes, Cancel",
    "danger"
  );

  if (!confirmed) return;

  try {
    const response = await fetch(
      `${API_BASE_URL}/bookings/${bookingId}/cancel`,
      {
        method: "POST",
      }
    );

    if (response.ok) {
      showToast("Success", "Booking cancelled successfully!", "success");
      showBookingTab("upcoming");
    } else {
      const error = await response.text();
      showToast("Error", `Failed to cancel booking: ${error}`, "danger");
    }
  } catch (error) {
    console.error("Error cancelling booking:", error);
    showToast("Error", "Connection error. Please try again.", "danger");
  }
}

async function completeBooking(bookingId) {
  const confirmed = await showConfirmDialog(
    "Complete Session",
    "Mark this session as completed? This will release payment to you.",
    "Mark Complete",
    "success"
  );

  if (!confirmed) return;

  try {
    const response = await fetch(
      `${API_BASE_URL}/bookings/${bookingId}/complete`,
      {
        method: "POST",
      }
    );

    if (response.ok) {
      showToast("Success", "Session marked as completed!", "success");
      showBookingTab("upcoming");
      loadTutorDashboard(); // Refresh to update earnings
    } else {
      const error = await response.text();
      showToast("Error", `Failed to complete booking: ${error}`, "danger");
    }
  } catch (error) {
    console.error("Error completing booking:", error);
    showToast("Error", "Connection error. Please try again.", "danger");
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
      showToast("Success", "Review submitted successfully!", "success");
    } else {
      const error = await response.text();
      showToast("Error", `Failed to submit review: ${error}`, "danger");
    }
  } catch (error) {
    console.error("Error submitting review:", error);
    showToast("Error", "Connection error. Please try again.", "danger");
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