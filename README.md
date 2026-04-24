# 🚗 Vehicle Identification System

## 📌 Project Overview
A comprehensive JavaFX desktop application for vehicle management, police records, and workshop services following MVC architecture.

## 🛠 Technologies Used
- **Java 21** - Core language
- **JavaFX 21** - GUI framework
- **PostgreSQL 14+** - Database
- **JDBC** - Database connectivity
- **Maven** - Build tool
- **Git & GitHub** - Version control

## 📊 Features Implemented (100% Complete)

### Core Requirements ✅
- ✅ MVC Architectural Pattern
- ✅ PostgreSQL with JDBC
- ✅ Stored Procedures (`sp_add_vehicle`, `sp_update_violation_status`)
- ✅ Database Views (`view_vehicle_owner`, `view_vehicle_violations`, `view_service_history`)
- ✅ Inheritance (BaseRecord abstract class)
- ✅ Polymorphism (getSummary() overrides)

### UI Components ✅
- ✅ Menu Bar with File/Admin/Modules/Help menus
- ✅ TableView for all data displays
- ✅ Pagination (5 items per page)
- ✅ ScrollPane with 25+ dummy elements
- ✅ Progress Bar & Progress Indicator
- ✅ DropShadow Effect on buttons
- ✅ FadeTransition Effect (continuous fade)

### Modules ✅
- **Admin** - User access management with GRANT/DENY
- **Customer** - Owner information management
- **Vehicle** - Vehicle registration and search
- **Police** - Reports and violations tracking
- **Workshop** - Service record management

## 🗄 Database Setup

### 1. Create Database
```sql
CREATE DATABASE vehicle_system;