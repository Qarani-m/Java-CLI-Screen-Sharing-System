import fs from "fs";
import moment from "moment";
import simpleGit from "simple-git";
import random from "random";

// Your Flutter project files (adjust paths if needed based on where you run the script)
const files = [
  "lib/main.dart",
  "lib/src/core/constants/strings.dart",
  "lib/src/core/constants/sizes.dart", 
  "lib/src/core/constants/images.dart",
  "lib/src/routes/routes.dart",
  "lib/src/shared/app_utils/app_utils.dart",
  "lib/src/shared/widgets/buttons.dart",
  "lib/src/shared/widgets/dialogs.dart",
  "lib/src/shared/widgets/inputs.dart",
  "lib/src/shared/styles/theme.dart",
  "lib/src/shared/styles/style_utils.dart",
  "lib/src/modules/auth/auth.dart",
  "lib/src/modules/auth/screens/Onboarding.dart",
  "lib/src/modules/auth/screens/login.dart",
  "lib/src/modules/auth/screens/forgot_password.dart",
  "lib/src/modules/auth/screens/register.dart",
  "lib/src/modules/savings/savings.dart",
  "lib/src/modules/music/music.dart"
];

// Random comment templates that won't affect Dart code functionality
const randomComments = [
  "// TODO: Consider performance optimization",
  "// NOTE: Code review completed", 
  "// FIXME: Documentation update needed",
  "// Refactored for better maintainability",
  "// Updated coding standards compliance",
  "// Enhanced error handling approach",
  "// Improved code readability",
  "// Added inline documentation",
  "// Performance optimization notes",
  "// Code structure enhancement",
  "// Updated method signatures",
  "// Refined implementation details",
  "// Enhanced user experience flow",
  "// Optimized widget performance",
  "// Improved state management",
  "// Updated UI consistency",
  "// Enhanced accessibility features",
  "// Refined animation timings",
  "// Improved error handling",
  "// Updated dependency management"
];

// Function to append a random comment to a file
const addRandomComment = (filePath) => {
  const comment = randomComments[random.int(0, randomComments.length - 1)];
  const timestamp = moment().format("YYYY-MM-DD HH:mm:ss");
  const commentLine = `\n// ${timestamp}: ${comment.replace("// ", "")}\n`;
  
  try {
    if (fs.existsSync(filePath)) {
      fs.appendFileSync(filePath, commentLine);
      return true;
    } else {
      console.warn(`File not found: ${filePath}`);
      return false;
    }
  } catch (error) {
    console.error(`Error writing to ${filePath}:`, error);
    return false;
  }
};

// Helper to commit on a given date
const commitOnDate = (date, filePath, callback) => {
  if (addRandomComment(filePath)) {
    const fileName = filePath.split('/').pop();
    const commitMessage = `Update ${fileName} - ${randomComments[random.int(0, randomComments.length - 1)].replace("// ", "")}`;
    
    simpleGit()
      .add([filePath])
      .commit(commitMessage, { "--date": date.format() }, callback);
  } else {
    callback();
  }
};

// Make commits between May 4 and June 7, 2025
const makeCommits = async () => {
  const startDate = moment("2025-05-04");
  const endDate = moment("2025-06-07");
  const totalDays = endDate.diff(startDate, 'days') + 1;
  
  console.log(`Creating backdated commits between ${startDate.format("YYYY-MM-DD")} and ${endDate.format("YYYY-MM-DD")}`);
  console.log(`Total days range: ${totalDays} days`);
  console.log(`Target: Fill approximately 1/3 of days with commits for realistic patterns`);
  
  let totalCommitsCreated = 0;
  let activeDays = 0;
  
  // Loop through each day in the range
  for (let day = 0; day < totalDays; day++) {
    const currentDate = startDate.clone().add(day, 'days');
    
    // REALISTIC PATTERN: Only commit on about 1/3 of days (33% chance)
    const shouldCommitToday = random.float(0, 1) < 0.70; // 33% chance to commit
    
    if (!shouldCommitToday) {
      console.log(`📅 ${currentDate.format("YYYY-MM-DD")}: No commits (inactive day)`);
      continue;
    }
    
    activeDays++;
    
    // SEVERITY CONTROL: Random commits per day when we do commit (1-6 commits per active day)
    const commitsToday = random.int(1, 6); // When we commit, do 1-6 commits
    
    // Create the commits for this day
    for (let i = 0; i < commitsToday; i++) {
      const commitDate = currentDate.clone();
      
      // Add random hours/minutes to spread commits throughout the day
      commitDate.add(random.int(9, 18), 'hours'); // Business hours
      commitDate.add(random.int(0, 59), 'minutes');
      commitDate.add(random.int(0, 59), 'seconds');
      
      // Pick a random file
      const randomFile = files[random.int(0, files.length - 1)];
      
      await new Promise(resolve => {
        setTimeout(() => {
          commitOnDate(commitDate, randomFile, resolve);
        }, 100); // Small delay to avoid overwhelming git
      });
      
      totalCommitsCreated++;
      console.log(`✓ Day ${day + 1}: Commit ${i + 1}/${commitsToday} - ${randomFile} on ${commitDate.format("YYYY-MM-DD HH:mm:ss")}`);
    }
    
    console.log(`📅 ${currentDate.format("YYYY-MM-DD")}: Created ${commitsToday} commits (Active day ${activeDays})`);
  }
  
  
  const percentageFilled = ((activeDays / totalDays) * 100).toFixed(1);
  
  console.log("\n🎉 All commits completed!");
  console.log("📋 Summary:");
  console.log(`   - Created ${totalCommitsCreated} total commits`);
  console.log(`   - Date range: ${startDate.format("YYYY-MM-DD")} to ${endDate.format("YYYY-MM-DD")}`);
  console.log(`   - Total days: ${totalDays} days`);
  console.log(`   - Active days: ${activeDays} days (${percentageFilled}% filled)`);
  console.log(`   - Average commits per active day: ${(totalCommitsCreated / activeDays).toFixed(1)}`);
  console.log(`   - Files modified: ${files.length} Flutter files`);
  console.log("\n💡 Next steps:");
  console.log("   1. Check your git log: git log --oneline");
  console.log("   2. Push to GitHub: git push origin main");
  console.log("   3. Check your contribution graph on GitHub!");
  
  // Uncomment the line below if you want to automatically push to remote
  // console.log("\n🚀 Pushing to remote...");
  // await simpleGit().push();
};

// Run the script
makeCommits().catch(console.error);