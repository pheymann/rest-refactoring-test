package com.github.pheymann.rrtt

import com.github.pheymann.rrtt.util.ResponseComparator.ComparisonResult

final case class TestResult(name: String,

                            successful: Boolean,
                            successfulTries: Int,

                            failedTries: Int,
                            comparisons: List[(RequestData, ComparisonResult)])
