# Kubernetes Cluster Setup from Scratch

![Kubernetes](https://img.shields.io/badge/kubernetes-%23326ce5.svg?style=for-the-badge&logo=kubernetes&logoColor=white)

A step-by-step guide to setting up a Kubernetes cluster from scratch using kubeadm on Ubuntu nodes.

## Table of Contents
- [Prerequisites](#prerequisites)
- [Cluster Setup](#cluster-setup)
  - [1. Prepare All Nodes](#1-prepare-all-nodes)
  - [2. Install Container Runtime](#2-install-container-runtime)
  - [3. Install Kubernetes Tools](#3-install-kubernetes-tools)
  - [4. Initialize Master Node](#4-initialize-master-node)
  - [5. Install CNI Plugin](#5-install-cni-plugin)
  - [6. Join Worker Nodes](#6-join-worker-nodes)
- [Verification](#verification)
- [Testing the Cluster](#testing-the-cluster)
- [Optional Components](#optional-components)
- [Troubleshooting](#troubleshooting)
- [Going to Production](#going-to-production)

## Prerequisites
- At least two Ubuntu 20.04/22.04 machines:
  - 1 Master Node (2+ vCPUs, 2GB+ RAM)
  - 1+ Worker Nodes (1+ vCPUs, 1GB+ RAM)
- Unique hostnames on all nodes
- SSH access between nodes
- Open ports: `6443`, `2379-2380`, `10250-10252`, `30000-32767`

## Cluster Setup

### 1. Prepare All Nodes
```bash
# Update system
sudo apt update && sudo apt upgrade -y

# Disable swap
sudo swapoff -a
sudo sed -i '/ swap / s/^\(.*\)$/#\1/g' /etc/fstab

# Install dependencies
sudo apt install -y apt-transport-https ca-certificates curl
