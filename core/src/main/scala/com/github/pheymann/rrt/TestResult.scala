package com.github.pheymann.rrt

import com.github.pheymann.rrt.util.ResponseComparator.ComparisonResult

final case class TestResult(name: String,

                            successful: Boolean,
                            successfulTries: Int,

                            failedTries: Int,
                            comparisons: List[(RequestData, ComparisonResult)])
