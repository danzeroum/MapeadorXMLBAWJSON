#!/bin/bash

# ============================================
# IBM BAW Analysis V3 - Build and Run Script
# ============================================

set -e  # Exit on any error

# Configuration
PROJECT_NAME="IBM BAW Analyzer V3"
MAIN_CLASS="br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.ImprovedBawAnalysisMain"
TEST_CLASS="br.com.danzeroum.bpmbaw.mapeadorxml.leitor.jsonIA.BawAnalysisIntegrationTest"
SRC_DIR="src"
BUILD_DIR="build"
LIB_DIR="lib"
OUTPUT_DIR="output"
DIST_DIR="dist"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Functions
print_header() {
    echo -e "${BLUE}============================================${NC}"
    echo -e "${BLUE}$1${NC}"
    echo -e "${BLUE}============================================${NC}"
}

print_step() {
    echo -e "${GREEN}📋 $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

# Check prerequisites
check_prerequisites() {
    print_step "Checking prerequisites..."
    
    if ! command -v java &> /dev/null; then
        print_error "Java is not installed or not in PATH"
        exit 1
    fi
    
    if ! command -v javac &> /dev/null; then
        print_error "Java compiler (javac) is not installed or not in PATH"
        exit 1
    fi
    
    echo "Java version:"
    java -version
    echo ""
    
    print_success "Prerequisites check passed"
}

# Create directory structure
setup_directories() {
    print_step "Setting up directory structure..."
    
    mkdir -p "$BUILD_DIR"
    mkdir -p "$OUTPUT_DIR"
    mkdir -p "$DIST_DIR"
    
    if [ ! -d "$SRC_DIR" ]; then
        print_error "Source directory '$SRC_DIR' not found"
        exit 1
    fi
    
    if [ ! -d "$LIB_DIR" ]; then
        print_warning "Library directory '$LIB_DIR' not found - creating empty directory"
        mkdir -p "$LIB_DIR"
    fi
    
    print_success "Directory structure ready"
}

# Build classpath
build_classpath() {
    CLASSPATH="$BUILD_DIR"
    
    if [ -d "$LIB_DIR" ]; then
        for jar in "$LIB_DIR"/*.jar; do
            if [ -f "$jar" ]; then
                CLASSPATH="$CLASSPATH:$jar"
            fi
        done
    fi
    
    echo "$CLASSPATH"
}

# Compile Java sources
compile_sources() {
    print_step "Compiling Java sources..."
    
    # Find all Java files
    JAVA_FILES=$(find "$SRC_DIR" -name "*.java" -type f)
    
    if [ -z "$JAVA_FILES" ]; then
        print_error "No Java source files found in $SRC_DIR"
        exit 1
    fi
    
    echo "Found $(echo "$JAVA_FILES" | wc -l) Java files"
    
    # Build classpath
    CLASSPATH=$(build_classpath)
    echo "Classpath: $CLASSPATH"
    
    # Compile with proper encoding and Java 8 compatibility
    javac -cp "$CLASSPATH" \
          -d "$BUILD_DIR" \
          -encoding UTF-8 \
          -source 8 \
          -target 8 \
          -Xlint:unchecked \
          $JAVA_FILES
    
    if [ $? -eq 0 ]; then
        print_success "Compilation completed successfully"
    else
        print_error "Compilation failed"
        exit 1
    fi
}

# Run integration tests
run_tests() {
    print_step "Running integration tests..."
    
    CLASSPATH=$(build_classpath)
    
    java -cp "$CLASSPATH" \
         -Xmx2g \
         -Dfile.encoding=UTF-8 \
         "$TEST_CLASS"
    
    if [ $? -eq 0 ]; then
        print_success "Integration tests passed"
    else
        print_warning "Integration tests failed or incomplete"
    fi
}

# Create executable JAR
create_jar() {
    print_step "Creating executable JAR..."
    
    # Create manifest
    MANIFEST_FILE="$BUILD_DIR/MANIFEST.MF"
    cat > "$MANIFEST_FILE" << EOF
Manifest-Version: 1.0
Main-Class: $MAIN_CLASS
Class-Path: .
Created-By: IBM BAW Analysis V3 Build Script
Implementation-Title: IBM BAW Process Analyzer
Implementation-Version: 3.0
Implementation-Vendor: Your Organization
EOF

    # Create JAR
    JAR_FILE="$DIST_DIR/baw-analyzer-v3.jar"
    
    cd "$BUILD_DIR"
    jar cfm "../$JAR_FILE" MANIFEST.MF $(find . -name "*.class")
    cd ..
    
    if [ -f "$JAR_FILE" ]; then
        print_success "JAR created: $JAR_FILE"
        echo "JAR size: $(du -h "$JAR_FILE" | cut -f1)"
    else
        print_error "Failed to create JAR"
        exit 1
    fi
}

# Run main analysis
run_analysis() {
    print_step "Running sample analysis..."
    
    CLASSPATH=$(build_classpath)
    
    echo "Available execution modes:"
    echo "1. Default (MeuProcessoBPM)"
    echo "2. OutroProjeto"
    echo "3. Test mode"
    echo "4. Custom parameters"
    
    read -p "Select mode (1-4): " mode
    
    case $mode in
        1)
            echo "Running default analysis..."
            java -cp "$CLASSPATH" \
                 -Xmx2g \
                 -Dfile.encoding=UTF-8 \
                 "$MAIN_CLASS"
            ;;
        2)
            echo "Running alternative project analysis..."
            java -cp "$CLASSPATH" \
                 -Xmx2g \
                 -Dfile.encoding=UTF-8 \
                 "$MAIN_CLASS" click2check
            ;;
        3)
            echo "Running test analysis..."
            java -cp "$CLASSPATH" \
                 -Xmx2g \
                 -Dfile.encoding=UTF-8 \
                 "$MAIN_CLASS" test
            ;;
        4)
            read -p "Project name: " project_name
            read -p "Process ID: " process_id
            read -p "Extraction path: " extraction_path
            
            echo "Running custom analysis..."
            java -cp "$CLASSPATH" \
                 -Xmx2g \
                 -Dfile.encoding=UTF-8 \
                 "$MAIN_CLASS" "$project_name" "$process_id" "$extraction_path" --verbose
            ;;
        *)
            print_warning "Invalid selection, skipping analysis"
            ;;
    esac
    
    if [ $? -eq 0 ]; then
        print_success "Analysis completed"
        
        # Show output files
        if [ -d "$OUTPUT_DIR" ]; then
            echo ""
            echo "Generated files:"
            ls -la "$OUTPUT_DIR"
        fi
    else
        print_error "Analysis failed"
    fi
}

# Clean build artifacts
clean() {
    print_step "Cleaning build artifacts..."
    
    rm -rf "$BUILD_DIR"
    rm -rf "$DIST_DIR"
    
    print_success "Clean completed"
}

# Show help
show_help() {
    echo "Usage: $0 [command]"
    echo ""
    echo "Commands:"
    echo "  build       Compile sources and create JAR"
    echo "  test        Run integration tests"
    echo "  run         Run analysis with interactive mode"
    echo "  jar         Create executable JAR only"
    echo "  clean       Clean build artifacts"
    echo "  all         Clean, build, test, and create JAR"
    echo "  help        Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0 build                    # Compile and build"
    echo "  $0 test                     # Run tests"
    echo "  $0 run                      # Interactive analysis"
    echo "  $0 all                      # Complete build pipeline"
}

# Main execution
main() {
    print_header "$PROJECT_NAME - Build Script"
    
    case "${1:-build}" in
        "clean")
            clean
            ;;
        "build")
            check_prerequisites
            setup_directories
            compile_sources
            print_success "Build completed"
            ;;
        "test")
            check_prerequisites
            setup_directories
            compile_sources
            run_tests
            ;;
        "jar")
            check_prerequisites
            setup_directories
            compile_sources
            create_jar
            ;;
        "run")
            check_prerequisites
            setup_directories
            compile_sources
            run_analysis
            ;;
        "all")
            check_prerequisites
            setup_directories
            compile_sources
            run_tests
            create_jar
            print_success "Complete build pipeline finished"
            ;;
        "help"|"-h"|"--help")
            show_help
            ;;
        *)
            print_error "Unknown command: $1"
            show_help
            exit 1
            ;;
    esac
}

# Execute main function with all arguments
main "$@"