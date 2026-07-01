# SeamCraft

Content-aware image resizing using seam carving, implemented in Java and compared across three algorithmic strategies: **Brute Force**, **Dynamic Programming**, and **Greedy**.

Seam carving resizes an image by removing the least visually important vertical seam, pixel by pixel, instead of naive cropping or scaling. This project implements the full pipeline from energy computation to seam removal, then benchmarks how each algorithmic approach performs in terms of speed, accuracy, and image quality.

---

## Table of Contents

- [Overview](#overview)
- [How It Works](#how-it-works)
- [Algorithms](#algorithms)
- [Time Complexity](#time-complexity)
- [Sample Results](#sample-results)
- [Comparison: DP vs Greedy](#comparison-dp-vs-greedy)
- [Getting Started](#getting-started)
- [Project Structure](#project-structure)
- [Team](#team)
- [Course Info](#course-info)

---

## Overview

Given an input image, SeamCraft computes an **energy map** that measures how visually important each pixel is (based on gradient intensity), then finds and removes the lowest-energy vertical seam. Repeating this process shrinks the image width while preserving its most important visual content.

Three approaches are implemented to find that seam:

| Algorithm | Strategy | Guarantees Optimal Seam |
|---|---|---|
| Brute Force | Explores all possible seam paths | Yes |
| Dynamic Programming | Builds a cumulative energy table | Yes |
| Greedy | Picks the locally best pixel at each row | No |

---

## How It Works

1. **Compute the energy matrix** — a Sobel-like operator measures brightness gradients for every pixel.
2. **Find the lowest-energy seam** — using Brute Force, DP, or Greedy.
3. **Remove the seam** — shift pixels to close the gap left by the removed column.
4. **Repeat** for the number of seams requested by the user.

---

## Algorithms

### Brute Force
Recursively explores every possible seam path (left, straight, right at each row) starting from every column in the top row, then keeps the path with the lowest total energy. Simple to reason about, but explores an exponential number of paths.

### Dynamic Programming
Builds a cumulative energy table row by row, where each cell stores the minimum path cost to reach it from the top. A parent table tracks the path so the optimal seam can be reconstructed by backtracking. Same optimal result as Brute Force, at a fraction of the cost.

### Greedy
Starts at the lowest-energy pixel in the top row, then at each subsequent row picks whichever neighboring pixel (left, center, right) has the lowest immediate energy — without considering the cumulative path cost. Very fast, but not guaranteed to find the true minimum-energy seam.

---

## Time Complexity

Let `w` = image width, `h` = image height.

| Algorithm | Best Case | Worst Case |
|---|---|---|
| Brute Force | O(w · 3ʰ) | O(w · 3ʰ) |
| Dynamic Programming | O(w · h) | O(w · h) |
| Greedy | O(w + h) | O(w + h) |

Brute Force becomes computationally infeasible past very small images (a 100x100 image alone requires over 10^49 operations), which is why it is only demonstrated on a 10x10 sample.

---

## Sample Results

| Approach | Input Size | Output Size | Seams Removed |
|---|---|---|---|
| Brute Force | 10x10 | 9x10 | 1 |
| Dynamic Programming | 504x378 | 404x378 | 100 |
| Greedy | 504x378 | 354x378 | 150 |

DP produces smooth, imperceptible resizing even on complex textures and patterns. Greedy runs faster but can introduce minor visual distortion on images with intricate detail.

---

## Comparison: DP vs Greedy

| Criteria | Dynamic Programming | Greedy |
|---|---|---|
| Accuracy | Finds the global optimal seam | Local optimum only, not guaranteed optimal |
| Time Complexity | O(w · h) | O(w + h) |
| Scalability | Slightly slower on very large images | Very fast even on large images |
| Result Quality | Smooth, optimal seams | Can produce jagged seams on complex images |
| Memory Usage | Higher — needs DP table + parent table | Lower — only tracks the current seam |
| Best For | Quality-critical resizing | Speed-critical resizing on large images |

---

## Getting Started

### Requirements
- Java JDK 8 or higher

### Run

```bash
javac SeamCarvingProject.java
java SeamCarvingProject
```

You'll be prompted to:
1. Choose an algorithm (Brute Force, Dynamic Programming, or Greedy)
2. Enter the number of seams to remove
3. Provide the path to your input image

The resized image is saved to the same directory as the input, tagged with the algorithm used (e.g. `image_DP.png`).

---

## Project Structure

```
SeamCraft/
├── SeamCarvingProject.java   # Main source file
├── samples/                  # Before/after sample images
└── README.md
```

---

## Team

| Name | Student ID | Contribution |
|---|---|---|
| Maha Aldakhil | 444202648 | Brute Force |
| Amina Abdelkareem | 444204580 | Dynamic Programming |
| Wajd Alquwayi | 443201429 | Greedy |

---

## Course Info

CSC311 — Design & Analysis of Algorithms
King Saud University, College of Computer and Information Science
Instructor: Dr. Ghada Alnifiel
Section: 44227
