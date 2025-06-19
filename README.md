# ScreenShare - Production-Grade CLI Screen Sharing System

[![Java](https://img.shields.io/badge/Java-11+-orange.svg)](https://www.oracle.com/java/)
[![Maven](https://img.shields.io/badge/Maven-3.6+-blue.svg)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)
[![Build Status](https://img.shields.io/badge/Build-Passing-brightgreen.svg)]()

A high-performance, production-grade screen sharing system built in Java with CLI interface. Supports multiple clients sharing their screens with a central server using TCP for control messages and UDP for screen data streaming.

## 🚀 Features

- **Multi-Client Support**: Multiple clients can connect and share screens simultaneously
- **High Performance**: Raw TCP/UDP sockets for maximum performance
- **Production Ready**: Robust error handling, automatic reconnection, and heartbeat monitoring
- **CLI Interface**: Clean command-line interface for both server and client
- **Configurable**: Extensive configuration options via files or command-line arguments
- **Layered Architecture**: Clean, maintainable codebase built from the ground up
- **Thread-Safe**: Concurrent handling of multiple clients with proper synchronization
- **Cross-Platform**: Works on Windows, macOS, and Linux
