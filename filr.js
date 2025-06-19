import fs from "fs";
import moment from "moment";
import simpleGit from "simple-git";
import random from "random";

// Java project files in the screenshare directory structure
const files = [
  "src/main/java/com/screenshare/server/ScreenShareServer.java",
  "src/main/java/com/screenshare/server/ClientHandler.java",
  "src/main/java/com/screenshare/server/ServerConfig.java",
  "src/main/java/com/screenshare/client/ScreenShareClient.java",
  "src/main/java/com/screenshare/client/ClientConfig.java",
  "src/main/java/com/screenshare/common/Message.java",
  "src/main/java/com/screenshare/common/MessageType.java",
  "src/main/java/com/screenshare/common/Protocol.java",
  "src/main/java/com/screenshare/common/NetworkBuffer.java",
  "src/main/java/com/screenshare/util/Logger.java"
];

// Java/server-side appropriate random comment templates
const randomComments = [
  "// TODO: Add unit tests",
  "// FIXME: Concurrency issue needs attention",
  "// NOTE: Code modularity improved",
  "// Refactored network layer",
  "// Enhanced thread safety",
  "// Optimized socket handling",
  "// Improved error logging",
  "// Updated server configuration handling",
  "// Codebase cleanup and style consistency",
  "// Improved object serialization logic",
  "// Simplified client-server handshake",
  "// Modularized protocol logic",
  "// Improved message parsing reliability",
  "// Logging mechanism refactored",
  "// Added null safety checks",
  "// Thread pooling enhanced",
  "// Performance tweaks for high load",
  "// Removed dead code from protocol",
  "// Updated JavaDoc comments",
  "// Introduced proper resource cleanup"
];

// Function to append a random comment to a Java file
const addRandomComment = (filePath) => {
  const comment = randomComments[random.int(0, randomComments.length - 1)];
  const timestamp = moment().format("YYYY-MM-DD HH:mm:ss");
  const commentLine = `\n/* ${timestamp}: ${comment.replace("// ", "")} */\n`;

  try {
    if (fs.existsSync(filePath)) {
      fs.appendFileSync(filePath, commentLine);
      return true;
    } else {
      console.warn(`⚠️ File not found: ${filePath}`);
      return false;
    }
  } catch (error) {
    console.error(`❌ Error writing to ${filePath}:`, error);
    return false;
  }
};

// Commit a change to a file on a specific date
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

// Generate random commits from May 4 to June 7, 2025
const makeCommits = async () => {
  const startDate = moment("2025-05-04");
  const endDate = moment("2025-06-07");
  const totalDays = endDate.diff(startDate, 'days') + 1;

  console.log(`🕒 Creating commits between ${startDate.format("YYYY-MM-DD")} and ${endDate.format("YYYY-MM-DD")}`);
  console.log(`📆 Total range: ${totalDays} days`);
  console.log(`🧠 Target realism: ~70% active days with 1-6 commits`);

  let totalCommitsCreated = 0;
  let activeDays = 0;

  for (let day = 0; day < totalDays; day++) {
    const currentDate = startDate.clone().add(day, 'days');

    const shouldCommitToday = random.float(0, 1) < 0.70;
    if (!shouldCommitToday) {
      console.log(`📅 ${currentDate.format("YYYY-MM-DD")}: Inactive`);
      continue;
    }

    activeDays++;
    const commitsToday = random.int(1, 6);

    for (let i = 0; i < commitsToday; i++) {
      const commitDate = currentDate.clone()
          .add(random.int(9, 18), 'hours') // Business hours
          .add(random.int(0, 59), 'minutes')
          .add(random.int(0, 59), 'seconds');

      const randomFile = files[random.int(0, files.length - 1)];

      await new Promise(resolve => {
        setTimeout(() => {
          commitOnDate(commitDate, randomFile, resolve);
        }, 100);
      });

      totalCommitsCreated++;
      console.log(`  ✓ ${commitDate.format("YYYY-MM-DD HH:mm:ss")} - ${randomFile}`);
    }

    console.log(`📅 ${currentDate.format("YYYY-MM-DD")}: ${commitsToday} commits`);
  }

  const percentageFilled = ((activeDays / totalDays) * 100).toFixed(1);

  console.log("\n✅ Commit Simulation Complete!");
  console.log("🔍 Summary:");
  console.log(`   • Total commits: ${totalCommitsCreated}`);
  console.log(`   • Active days: ${activeDays} / ${totalDays} (${percentageFilled}%)`);
  console.log(`   • Average commits/day: ${(totalCommitsCreated / activeDays).toFixed(1)}`);
  console.log(`   • Java files touched: ${files.length}`);
  console.log("\n🚀 Next Steps:");
  console.log("   1. Inspect history: git log --oneline");
  console.log("   2. Push to GitHub: git push origin main");
  console.log("   3. Check your GitHub contribution graph!");

  // Uncomment to auto-push after all commits
  // await simpleGit().push();
};

// Execute
makeCommits().catch(console.error);
